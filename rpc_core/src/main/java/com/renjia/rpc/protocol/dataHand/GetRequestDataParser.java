package com.renjia.rpc.protocol.dataHand;

import com.renjia.rpc.anno.ExposeParam;
import com.renjia.rpc.util.RouterInit;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public class GetRequestDataParser implements ControllerParamParser {
    private HashMap<String, String> result = new HashMap<>();

    @Override
    public Object getParamValueByMethod(Method method) {
        Object[] paramValues = new Object[method.getParameterCount()];
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            ExposeParam annotation = parameters[i].getAnnotation(ExposeParam.class);
            if (annotation == null) {
                paramValues[i] = null;
                continue;
            }
            String typeName = parameters[i].getParameterizedType().getTypeName();
            String value = result.get(annotation.name());
            if (value == null) {
                paramValues[i] = null;
                continue;
            }
            Object apply = parser.get(typeName).apply(value);
            paramValues[i] = apply;
        }

        return paramValues.length == 0 ? null : paramValues;
    }

    @SneakyThrows
    @Override
    public void handle(RouterInit.RequestHandler context) {
        /*name=renjia&zyh=love*/
        String data = null;
        byte[] bytes = context.getFuture().get();
        if (bytes.length != 0) {
            data = new String(bytes);
        } else {
            data = context.getQueryRequestData();
        }
        if (data == null) return;
        HashMap<String, String> result = new HashMap<>();
        if (data.contains("&")) {
            for (String s : data.split("&")) {
                String[] split = s.split("=");
                result.put(split[0], split[1]);
            }
        } else {
            String[] split = data.split("=");
            result.put(split[0], split[1]);
        }
        this.result.putAll(result);
    }

    @Override
    public boolean isAccordWith(RouterInit.RequestHandler context) {
        return !"enable".equals(context.getHeaders().get("isrpc")) && (context.getContentType() == null ||
                "application/x-www-form-urlencoded".equals(context.getContentType()));
    }
}
