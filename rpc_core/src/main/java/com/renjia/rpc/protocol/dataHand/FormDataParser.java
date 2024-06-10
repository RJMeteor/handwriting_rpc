package com.renjia.rpc.protocol.dataHand;

import com.renjia.rpc.anno.ExposeParam;
import com.renjia.rpc.util.RouterInit;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FormDataParser implements ControllerParamParser {
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
        return paramValues;
    }

    @SneakyThrows
    @Override
    public void handle(RouterInit.RequestHandler context) {
        /*
        ----------------------------876502053268893838035583
        Content-Disposition: form-data; name="name"

        34
        ----------------------------876502053268893838035583
        Content-Disposition: form-data; name="dddd"

        fjak
        ----------------------------876502053268893838035583--
        */
        byte[] bytes = context.getFuture().get();
        if (bytes.length == 0) return;
        String[] split = new String(bytes).split("\n");
        String[] source = new String[split.length - 2];
        System.arraycopy(split, 1, source, 0, source.length);
        List<String> targer = Arrays.asList(source)
                .stream().filter(ter -> !"".equals(ter.trim()))
                .filter(ter->!ter.trim().contains(context.getContentType().substring(31).trim()))
                .collect(Collectors.toList());

        String name = null;
        HashMap<String, String> tre = new HashMap<>();

        for (int i = 0; i < targer.size(); i++) {
            String s1 = targer.get(i);
            int surlen = s1.length();
            if (i%2 == 0) {
                name = s1.substring(s1.indexOf("\"") + 1, surlen - 2);
            } else {
                tre.put(name, s1.trim());
                name = null;
            }
        }
        this.result.putAll(tre);
    }

    @Override
    public boolean isAccordWith(RouterInit.RequestHandler context) {
        String contentType = context.getContentType();
        return !"enable".equals(context.getHeaders().get("isrpc")) && (contentType != null && contentType.contains("multipart/form-data"));
    }
}
