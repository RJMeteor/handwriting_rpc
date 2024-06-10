package com.renjia.rpc.protocol;

import com.alibaba.fastjson.JSON;
import com.renjia.rpc.factory.ProxyFactory;
import com.renjia.rpc.protocol.dataHand.ControllerParamParser;
import com.renjia.rpc.util.RouterInit;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class OrdinaryChaiHandler implements RequestChaiHandler<RouterInit.RequestHandler> {
    private Class target;
    private Method method;
    private Object targetInstance;

    public OrdinaryChaiHandler(Class target, Method method) {
        this.target = target;
        this.method = method;
    }


    @SneakyThrows
    @Override
    public void handle(RouterInit.RequestHandler context) {
        this.method.setAccessible(true);
        targetInstance = RouterInit.CATCHREQUESTBEAN.get(target);
        if (targetInstance == null) {
            targetInstance = target.newInstance();
            RouterInit.CATCHREQUESTBEAN.put(target, targetInstance);
            ProxyFactory.createProxy(target, targetInstance);
        }

        RequestChaiHandler requestChaiHandler = RouterInit.handlerRequeDataLocal.get();
        ControllerParamParser doHandlerTarger = (ControllerParamParser) requestChaiHandler;
        Object paramValue = doHandlerTarger.getParamValueByMethod(method);

        Object[] objects = null;
        if (paramValue.getClass().getName().startsWith("[L")) {
            Object[] paramValue1 = (Object[]) paramValue;
            objects = new Object[paramValue1.length];
            for (int i = 0; i < ((Object[]) paramValue).length; i++) {
                objects[i] = paramValue1[i];
            }
        }

        Object invoke = null;
        if (paramValue == null) invoke = this.method.invoke(targetInstance);
        else if (objects != null) invoke = this.method.invoke(targetInstance, objects);
        else invoke = this.method.invoke(targetInstance, new Object[]{paramValue});

        if (invoke != null) {
            byte[] serialize = JSON.toJSONBytes(invoke);
            Buffer out = new BufferImpl();
            out.setBytes(0, serialize);
            context.getResponse().end(out);
            return;
        }

        context.getResponse().end();
    }

    @Override
    public boolean isAccordWith(RouterInit.RequestHandler context) {
        return context.getHeaders().get("isrpc") == null;
    }
}
