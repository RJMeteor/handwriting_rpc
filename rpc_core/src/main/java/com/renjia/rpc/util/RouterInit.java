package com.renjia.rpc.util;

import com.renjia.rpc.anno.ExposePoint;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.protocol.ComplexChaiHandler;
import com.renjia.rpc.protocol.OrdinaryChaiHandler;
import com.renjia.rpc.protocol.RequestChaiHandler;
import com.renjia.rpc.protocol.dataHand.ControllerParamParser;
import com.renjia.rpc.protocol.dataHand.FormDataParser;
import com.renjia.rpc.protocol.dataHand.GetRequestDataParser;
import com.renjia.rpc.protocol.dataHand.JsonDataParser;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RouterInit {

    public static ThreadLocal<RequestChaiHandler> handlerRequeDataLocal = new ThreadLocal();


    private static ExecutorService service = new ThreadPoolExecutor(2,
            10,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(12));

    public static final HashMap<RequestInfo.RequestType, HttpMethod> REQUESTTYPE = new HashMap<>();
    public static final HashMap<Class, Object> CATCHREQUESTBEAN = new HashMap<>();


    static {
        REQUESTTYPE.put(RequestInfo.RequestType.GET, HttpMethod.GET);
        REQUESTTYPE.put(RequestInfo.RequestType.POST, HttpMethod.POST);
        REQUESTTYPE.put(RequestInfo.RequestType.PUT, HttpMethod.PUT);
        REQUESTTYPE.put(RequestInfo.RequestType.DELETE, HttpMethod.DELETE);
    }


    public static void routerInit(Router router, Class target, Method method, ExposePoint point) {
        router.route(REQUESTTYPE.get(point.pointType()), point.pointUrl()).handler(new RequestHandler(target, method));
    }


    @Data
    public static class RequestHandler implements Handler<RoutingContext> {
        //普通请求和远程调用处理链
        private HandlerChai handlerChai;
        //请求数据封装链
        private HandlerChai requestHandlerChai;
        //效应实例
        private HttpServerResponse response;
        //请求数据类型
        private String contentType;
        //请求头列表
        private MultiMap headers;
        //get请求数据
        private String queryRequestData;
        //消息体数据
        private CompletableFuture<byte[]> future = new CompletableFuture<>();

        public RequestHandler(Class target, Method method) {
            handlerChai = new HandlerChai()
                    .register(new ComplexChaiHandler(target, method))
                    .register(new OrdinaryChaiHandler(target, method)).first;

            requestHandlerChai = new HandlerChai()
                    .register(new FormDataParser())
                    .register(new GetRequestDataParser())
                    .register(new JsonDataParser()).first;
        }

        @SneakyThrows
        @Override
        public void handle(RoutingContext routingContext) {
            HttpServerRequest request = routingContext.request();
            this.headers = request.headers();
            this.contentType = headers.get("Content-Type");
            request.bodyHandler(buffer -> {
                byte[] bytes = buffer.getBytes();
                future.complete(bytes);
            });
            this.queryRequestData = request.query();

            this.response = routingContext.response();

            /*这里处理前端传过来的请求产生，并向下传递*/
            /*
             * 简单的方式:
             * 规定请求方式为post
             * 提供服务的接口上的注解增加一个字段为要传递给参数类型
             * 为方便起见方法参数是一个，便于方法的参数传递
             * 后端根据该类型进行反序列化
             * */
            service.execute(() -> {
                requestHandlerChai.doHandler(this);
                handlerChai.doHandler(this);
                handlerRequeDataLocal.remove();
            });
        }
    }

    @ToString
    public static class HandlerChai {
        public HandlerChai first;
        //最后正在处理请求的对象
        private HandlerChai last;
        private RequestChaiHandler<RequestHandler> value;
        public RequestChaiHandler<RequestHandler> doHandlerTarger;

        public HandlerChai() {
        }

        public HandlerChai register(RequestChaiHandler<RequestHandler> handler) {
            if (this.first == null) {
                this.first = this;
            }
            this.value = handler;
            this.last = new HandlerChai();
            this.last.first = this.first;
            return this.last;
        }

        @SneakyThrows
        public void doHandler(RequestHandler context) {
            if (this.value == null) return;
            if (this.value.isAccordWith(context)) {
                this.value.handle(context);
                handlerRequeDataLocal.set(this.value);
                return;
            }
            HandlerChai last = this.last;
            if (last == null) new RuntimeException("mei");
            last.doHandler(context);
        }
    }
}
