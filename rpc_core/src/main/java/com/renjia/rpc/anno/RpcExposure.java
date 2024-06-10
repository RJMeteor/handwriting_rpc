package com.renjia.rpc.anno;

import java.lang.annotation.*;

/*
 * 提供服务（服务端）
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.FIELD})
public @interface RpcExposure {
}
