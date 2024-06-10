package com.renjia.rpc.protocol.dataHand;

import com.renjia.rpc.anno.ExposeParam;
import com.renjia.rpc.factory.LoadResourceFactory;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.util.OridinrySerializer;
import com.renjia.rpc.util.RouterInit;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class JsonDataParser implements ControllerParamParser {
    private Object result = null;

    @Override
    public Object getParamValueByMethod(Method method) {
        Object[] paramValues = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        if (result == null || parameters.length > 1) return null;
        paramValues[0] = result;
        return paramValues;
    }

    @SneakyThrows
    @Override
    public void handle(RouterInit.RequestHandler context) {
        /*
        * {
             "name":"renjia"
            }
        * */
        byte[] bytes = context.getFuture().get();
        if (bytes.length == 0 || new String(bytes).startsWith("{")) return;
        OridinrySerializer oridinrySerializer = new OridinrySerializer();
        Object deserialize = oridinrySerializer.deserialize(bytes);
        this.result = deserialize;
    }

    @Override
    public boolean isAccordWith(RouterInit.RequestHandler context) {
        return !"enable".equals(context.getHeaders().get("isrpc")) && "application/json".equals(context.getContentType());
    }
}
