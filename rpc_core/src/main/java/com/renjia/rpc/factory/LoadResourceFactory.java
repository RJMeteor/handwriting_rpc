package com.renjia.rpc.factory;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.*;
import java.util.HashMap;
import java.util.Optional;

@Data
public class LoadResourceFactory {
    //加载资源路径
    private static final String RESOURCEURL = "META-INF\\rpc_hand\\";
    private static final HashMap<String, HashMap<String, Object>> RESOURCEMAPPING = new HashMap<String, HashMap<String, Object>>();

    public static <T> T load(Class<T> group, String type) {
        String fileName = group.getName();
        HashMap<String, Object> stringObjectHashMap = RESOURCEMAPPING.get(fileName);
        Boolean enpoint = false;
        if (stringObjectHashMap == null) {
            enpoint = true;
            stringObjectHashMap = new HashMap<String, Object>();
            RESOURCEMAPPING.put(fileName, stringObjectHashMap);
            return LoadResourceFactory.<T>registerNeedSPI(stringObjectHashMap, fileName, type);
        }
        T result = (T) stringObjectHashMap.get(type);
        if (!enpoint && result == null) {
            result = LoadResourceFactory.<T>registerNeedSPI(stringObjectHashMap, fileName, type);
        }
        return result;
    }

    @SneakyThrows
    private static <T> T registerNeedSPI(HashMap hashMap, String fileName, String type) {
        InputStream stream = ResourceUtil.getStream(targetResource(fileName));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String value = null;
        while ((value = bufferedReader.readLine()) != null) {
            String[] split = value.split("=");
            if (split[0].equals(type)) {
                Object o = Class.forName(split[1]).newInstance();
                hashMap.put(split[0], o);
                return (T) o;
            }
        }
        return null;
    }

    public static<T> void register(Class<T> group, String key, Object resource) {
        Optional.ofNullable(RESOURCEMAPPING.get(group.getName()))
                .orElseGet(() -> RESOURCEMAPPING.put(group.getName(), new HashMap<String, Object>()))
                .put(key, resource);
    }

    private static String targetResource(String fileName) {
        return new StringBuffer(RESOURCEURL).append(fileName).toString();
    }
}
