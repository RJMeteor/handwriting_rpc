package com.renjia.rpc.apply;

import com.renjia.rpc.apply.config.SpringStarterBeanRpcServer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SpringStarterBeanRpcServer.class})
public @interface EnableRPCAutoServer {
    String scanPackage() default "com.renjia.sourcecode_java.fetch";
}
