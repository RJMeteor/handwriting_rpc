package com.renjia.rpc.apply.handle;

import com.alibaba.fastjson.JSON;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.protocol.RpcProtocol;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PutRequestHandler extends RequestHandler {

    @Override
    public void handle(RpcProtocol rpcProtocol, CompletableFuture<RpcProtocol> complet) {
        // 创建 LocalVariableTableParameterNameDiscoverer 实例
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        // 获取参数名
        String[] parameterNames = discoverer.getParameterNames(rpcProtocol.getMethod());
        Object[] args = rpcProtocol.getArgs();

        String requestUrl = new StringBuilder("http://").append(rpcProtocol.getIp())
                .append(":").append(rpcProtocol.getPort())
                .append(rpcProtocol.getRequestUrl())
                .toString();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], args[i]);
        }
        String jsonString = JSON.toJSONString(map);
        HttpEntity<Object> requestEntity = new HttpEntity<>(jsonString, requestHeaders);

        RestTemplate restTemplate =  this.applicationContext.getBean(RestTemplate.class);
        ResponseEntity exchange = restTemplate.exchange(requestUrl, HttpMethod.PUT, requestEntity, rpcProtocol.getReturnType());
        rpcProtocol.getBody().setBody(exchange.getBody());
        complet.complete(rpcProtocol);
    }

    @Override
    public Boolean isSuport(RpcProtocol rpcProtocol) {
        return rpcProtocol.getRequestType() == RequestInfo.RequestType.PUT;
    }

}
