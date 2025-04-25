package com.renjia.rpc.anno;

import com.renjia.rpc.loadBalancer.LoadBalancer;
import com.renjia.rpc.loadBalancer.RandomLoadBalancer;
import com.renjia.rpc.retry.Retry;
import com.renjia.rpc.serializer.KryoSerializer;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.tolerant.DefaultValueFaultTolerant;
import com.renjia.rpc.tolerant.FaultTolerant;

import java.lang.annotation.*;

/*
 * 客户端请求远程服务策略
 * */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcFetch {
    //服务名
    String serverName();

    //负载均衡器
    LoadBalancer.Type loadBalancer() default LoadBalancer.Type.RANDOM;

    //重试机制
    Retry.Type retry() default Retry.Type.NO;

    //容错，降级
    Class tolerant() default DefaultValueFaultTolerant.class;
}
