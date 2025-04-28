package com.renjia.rpc.apply.config;

import com.renjia.rpc.apply.RequestHttpClient;
import com.renjia.rpc.apply.handle.*;
import com.renjia.rpc.apply.init.InitRpcResource;
import com.renjia.rpc.apply.init.SpringStaterRpcBeanServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnClass(name = {"org.springframework.web.servlet.DispatcherServlet"})
@EnableConfigurationProperties(value = {SpringStaterRpcBeanServerConfig.class})
public class RPCConfiguration {

    @Bean
    public SpringStaterRpcBeanServerConfig config() {
        return new SpringStaterRpcBeanServerConfig();
    }


    @Bean
    public InitRpcResource initRpcResource(SpringStaterRpcBeanServerConfig config) {
        return new InitRpcResource(config);
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }


    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        return factory;
    }

    @Configuration
    public static class RequestInit {
        @Bean
        public RequestHttpClient requestHttpClient() {
            return new RequestHttpClient();
        }

        @Bean
        public RequestHandler deleteRequestHandler() {
            return new DeleteRequestHandler();
        }

        @Bean
        public RequestHandler getRequestHandler() {
            return new GetRequestHandler();
        }

        @Bean
        public RequestHandler postRequestHandler() {
            return new PostRequestHandler();
        }

        @Bean
        public RequestHandler putRequestHandler() {
            return new PutRequestHandler();
        }
    }

}
