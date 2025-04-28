package com.renjia.rpc.core;

import com.renjia.rpc.factory.LoadResourceFactory;
import com.renjia.rpc.loadBalancer.LoadBalancer;
import com.renjia.rpc.protocol.RpcProtocol;
import com.renjia.rpc.registrationCenter.Register;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.util.RouterInit;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import io.vertx.core.http.HttpClientRequest;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class HttpClient {

    @SneakyThrows
    public RpcProtocol request(RpcProtocol rpcProtocol) throws InterruptedException, ExecutionException {
        Register register = LoadResourceFactory.load(Register.class, StorageRegisterInfo.initConfig.getRegiste().getRegisterType().getType());
        LoadBalancer loadBalancer = LoadResourceFactory.load(LoadBalancer.class, rpcProtocol.getLoadBalancer().getType());

        List<String> discovery = register.discovery(rpcProtocol.getServerName());

        Object choice = loadBalancer.choice(discovery);

        register.register(rpcProtocol.getServerName(), (String) choice);


        String[] hostname = ((String) choice).split(":");
        rpcProtocol.setIp(hostname[0]);
        rpcProtocol.setPort(hostname[1]);

        return this.doRequest(rpcProtocol);
    }

    @SneakyThrows
    protected RpcProtocol doRequest(RpcProtocol rpcProtocol) {
        CompletableFuture<RpcProtocol> responseFuture = new CompletableFuture<>();

        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpClient httpClient = vertx.createHttpClient();

        httpClient.request(RouterInit.REQUESTTYPE.get(rpcProtocol.getRequestType()),
                Integer.parseInt(rpcProtocol.getPort()),
                rpcProtocol.getIp(),
                rpcProtocol.getRequestUrl(), new HanlderComplx(rpcProtocol, responseFuture));
        RpcProtocol result = responseFuture.get();
        return result;
    }


    static class HanlderComplx implements Handler<AsyncResult<HttpClientRequest>> {
        private RpcProtocol protocol;
        private CompletableFuture<RpcProtocol> responseFuture;

        public HanlderComplx(RpcProtocol protocol, CompletableFuture<RpcProtocol> responseFuture) {
            this.protocol = protocol;
            this.responseFuture = responseFuture;
        }

        @SneakyThrows
        @Override
        public void handle(AsyncResult<HttpClientRequest> result) {
            if (!result.succeeded()) return;
            Serializer serializer = LoadResourceFactory.load(Serializer.class, StorageRegisterInfo.initConfig.getRegiste().getSerializerType().getType());
            HttpClientRequest httpClientRequest = result.result();
            httpClientRequest.response(handl -> {
                if (handl.succeeded()) {
                    handl.result().body().onSuccess(dg -> {
                        Serializer load = LoadResourceFactory.load(Serializer.class, StorageRegisterInfo.initConfig.getRegiste().getSerializerType().getType());
                        RpcProtocol deserialize = load.deserialize(dg.getBytes());
                        responseFuture.complete(deserialize);
                    });
                }
            });

            MultiMap headers = httpClientRequest.headers();
            headers.add("isrpc", "enable");
            byte[] serialize = serializer.serialize(protocol);
            BufferImpl buffer = new BufferImpl();
            Buffer re = buffer.setBytes(0, serialize);
            httpClientRequest.end(re);
        }
    }
}
