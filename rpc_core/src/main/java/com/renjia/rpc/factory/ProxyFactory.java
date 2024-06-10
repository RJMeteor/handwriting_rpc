package com.renjia.rpc.factory;

import cn.hutool.core.lang.hash.Hash;
import com.renjia.rpc.anno.RpcExposure;
import com.renjia.rpc.core.JavassistInstance;
import com.renjia.rpc.protocol.RequestInvocationHandler;
import lombok.SneakyThrows;

import java.lang.reflect.*;
import java.util.HashMap;

public class ProxyFactory {

    private static final HashMap<Class, Object> cacheProxy = new HashMap<>();

    @SneakyThrows
    public static <T> T getProxy(Class<T> target) {
        Object cache = cacheProxy.get(target);
        if (cache != null) return (T) cache;
        Class<?> proxyClass = Proxy.getProxyClass(target.getClassLoader(), target.getInterfaces());
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        Object t = constructor.newInstance(new RequestInvocationHandler(target));
        Object put = cacheProxy.put(target, t);
        return (T) t;
    }

    @SneakyThrows
    public static void createProxy(Class target, Object targetInstance) {
        for (Field field : target.getDeclaredFields()) {
            field.setAccessible(true);
            RpcExposure annotation = field.getAnnotation(RpcExposure.class);
            if (annotation == null && !Modifier.isInterface(field.getType().getModifiers())) continue;
            field.set(
                    targetInstance,
                    ProxyFactory.getProxy(
                            new JavassistInstance(field.getType())
                                    .dynamicGenerateClass()
                                    .getClass()));
        }
    }
}
