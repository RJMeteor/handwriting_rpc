package com.renjia.rpc.anno;


import java.lang.annotation.*;

/*
 * 提供服务的暴露点（服务端）
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExposePoint {
    String pointUrl();
    RequestInfo.RequestType pointType() default RequestInfo.RequestType.GET;
}
