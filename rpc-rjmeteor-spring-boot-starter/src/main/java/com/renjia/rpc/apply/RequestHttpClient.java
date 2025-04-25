package com.renjia.rpc.apply;

import com.alibaba.fastjson.JSON;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.core.HttpClient;
import com.renjia.rpc.protocol.RpcProtocol;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;

public class RequestHttpClient extends HttpClient implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected RpcProtocol doRequest(RpcProtocol rpcProtocol, String[] hostname) {
        String requestUrl = new StringBuilder("http://").append(hostname[0])
                .append(":").append(hostname[1])
                .append(rpcProtocol.getRequestUrl())
                .toString();
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);

//        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<Object> requestEntity = null;
        HttpHeaders requestHeaders = new HttpHeaders();

        HttpMethod method = null;
        if (rpcProtocol.getRequestType() == RequestInfo.RequestType.GET) method = HttpMethod.GET;
        else if (rpcProtocol.getRequestType() == RequestInfo.RequestType.POST) method = HttpMethod.POST;
        else if (rpcProtocol.getRequestType() == RequestInfo.RequestType.DELETE) method = HttpMethod.DELETE;
        // 创建 LocalVariableTableParameterNameDiscoverer 实例
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        // 获取参数名
        String[] parameterNames = discoverer.getParameterNames(rpcProtocol.getMethod());
        Object[] args = rpcProtocol.getArgs();
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
            for (int i = 0; i < parameterNames.length; i++) {
                builder.queryParam(parameterNames[i], args[i]);
            }
            requestUrl = builder.toUriString();
            requestEntity = new HttpEntity<>(null, requestHeaders);
        } else if (method == HttpMethod.POST) {
            requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HashMap<String, Object> map = new HashMap<>();
            for (int i = 0; i < parameterNames.length; i++) {
                map.put(parameterNames[i], args[i]);
            }
            String jsonString = JSON.toJSONString(map);
            requestEntity = new HttpEntity<>(jsonString, requestHeaders);
        }
        RpcProtocol.Body build = RpcProtocol.Body.builder().build();
        rpcProtocol.setBody(build);
        try {
            ResponseEntity exchange = restTemplate.exchange(requestUrl, method, requestEntity, rpcProtocol.getReturnType());
            rpcProtocol.getBody().setBody(exchange.getBody());
        } catch (RestClientException e) {
            rpcProtocol.getBody().setThrowable(e);
        }

        Object body = rpcProtocol.getBody().getBody();

        return rpcProtocol;
    }

}
