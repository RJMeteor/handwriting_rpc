package com.renjia.rpc.apply.handle;

import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class DeleteRequestHandler extends RequestHandler {
    @SneakyThrows
    @Override
    public void handle(RpcProtocol rpcProtocol, CompletableFuture<RpcProtocol> complet) {
        // 创建 LocalVariableTableParameterNameDiscoverer 实例
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        // 获取参数名

        Method method = Class.forName(rpcProtocol.getMethodClass()).getMethod(rpcProtocol.getMethodName(), rpcProtocol.getParamType());
        String[] parameterNames = discoverer.getParameterNames(method);
        Object[] args = rpcProtocol.getArgs();

        String requestUrl = new StringBuilder("http://").append(rpcProtocol.getIp())
                .append(":").append(rpcProtocol.getPort())
                .append(rpcProtocol.getRequestUrl())
                .toString();

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
        for (int i = 0; i < parameterNames.length; i++) {
            builder.queryParam(parameterNames[i], args[i]);
        }
        requestUrl = builder.toUriString();
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, requestHeaders);

        RestTemplate restTemplate = this.applicationContext.getBean(RestTemplate.class);
        ResponseEntity exchange = restTemplate.exchange(requestUrl, HttpMethod.DELETE, requestEntity, rpcProtocol.getReturnType());
        rpcProtocol.getBody().setBody(exchange.getBody());
        complet.complete(rpcProtocol);
    }

    @Override
    public Boolean isSuport(RpcProtocol rpcProtocol) {
       return   rpcProtocol.getRequestType() == RequestInfo.RequestType.DELETE;
    }

}
