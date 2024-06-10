package com.renjia.rpc.protocol.dataHand;

import cn.hutool.core.map.MapUtil;
import com.renjia.rpc.protocol.RequestChaiHandler;
import com.renjia.rpc.util.RouterInit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface ControllerParamParser extends RequestChaiHandler<RouterInit.RequestHandler> {
    Map<String, Function<String, Object>> parser = MapUtil.builder(new HashMap<String, Function<String, Object>>())
            .put("java.lang.String", String::toString)
            .put("java.lang.Integer", Integer::parseInt)
            .put("java.lang.Boolean", Boolean::parseBoolean)
            .put("java.lang.Byte", Byte::parseByte)
            .put("java.lang.Short", Short::parseShort)
            .put("java.lang.Long", Long::parseLong)
            .put("int", Integer::parseInt)
            .put("boolean", Boolean::parseBoolean)
            .put("byte", Byte::parseByte)
            .put("short", Short::parseShort)
            .put("long", Long::parseLong)
            .build();

    Object getParamValueByMethod(Method method);
}
