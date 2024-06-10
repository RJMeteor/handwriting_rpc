package com.renjia.rpc.protocol;

import com.renjia.rpc.core.StorageRegisterInfo;
import com.renjia.rpc.factory.LoadResourceFactory;
import com.renjia.rpc.factory.ProxyFactory;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.util.RouterInit;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferImpl;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class ComplexChaiHandler implements RequestChaiHandler<RouterInit.RequestHandler> {
    private Class target;
    private Method method;
    private Serializer json;
    private Object targetInstance;

    public ComplexChaiHandler(Class target, Method method) {
        this.target = target;
        this.method = method;
        this.json = LoadResourceFactory.load(Serializer.class, StorageRegisterInfo.initConfig.getRegiste().getSerializerType().getType());
    }

    @SneakyThrows
    @Override
    public void handle(RouterInit.RequestHandler context) {
        this.method.setAccessible(true);
        targetInstance = RouterInit.CATCHREQUESTBEAN.get(target);
        if (targetInstance == null) {
            targetInstance = target.newInstance();
            ProxyFactory.createProxy(target, targetInstance);
        }

        byte[] bytes = context.getFuture().get();
        RpcProtocol deserialize = json.deserialize(bytes);
        Object[] args = deserialize.getArgs();


        RpcProtocol protocol = new RpcProtocol();
        RpcProtocol.Body body = RpcProtocol.Body.builder().build();
        try {
            Object invoke = this.method.invoke(targetInstance, args);
            body.setBody(invoke);
        } catch (Exception ex) {
            ex.printStackTrace();
            body.setThrowable(ex);
        }
        protocol.setBody(body);
        byte[] serialize = json.serialize(protocol);
        Buffer out = new BufferImpl();
        out.setBytes(0, serialize);
        context.getResponse().end(out);
    }

    @Override
    public boolean isAccordWith(RouterInit.RequestHandler context) {
        return "enable".equals(context.getHeaders().get("isrpc"));
    }

}
