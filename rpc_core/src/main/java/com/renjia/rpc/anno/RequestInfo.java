package com.renjia.rpc.anno;

import lombok.Getter;

import java.io.Serializable;
import java.lang.annotation.*;


/*
 * 客户端请求远程路径
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestInfo {

    @Getter
     enum RequestType implements Serializable {
        POST("post"), GET("get"), DELETE("delete"), PUT("put");
        private static final long serialVersionUID = 1L;
        String type;

        RequestType(String type) {
            this.type = type;
        }
    }

    RequestType requestType() default RequestType.GET;

    String requesetUrl();
}
