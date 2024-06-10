package com.renjia.rpc.anno;

import java.lang.annotation.*;

/*
 * 提供服务包（服务端）
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProducerScanPackage {
    String[] packages();
}
