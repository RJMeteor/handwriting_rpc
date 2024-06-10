package com.renjia.rpc.anno;


import java.lang.annotation.*;

/*
 * 提供服务的暴露点（服务端）
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ExposeParam {
    String name();
}
