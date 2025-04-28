package com.renjia.rpc.protocol;

import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.loadBalancer.LoadBalancer;
import com.renjia.rpc.retry.Retry;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.tolerant.FaultTolerant;
import lombok.*;
import sun.reflect.generics.tree.ReturnType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcProtocol implements Serializable {
    private static final long serialVersionUID = 1L;
    // 调用代理的方法
    private Method method;
    // 调用代理的方法的返回值类型
    private Class returnType;
    // 调用目标ip地址
    private String ip;
    // 调用目标端口
    private String port;
    //服务名
    private String serverName;
    //调用目标URL
    private String requestUrl;
    //调用目标URL类型
    private RequestInfo.RequestType requestType;
    //负载均衡器
    private LoadBalancer.Type loadBalancer = LoadBalancer.Type.RANDOM;
    //重试
    private Retry.Type retry = Retry.Type.NO;
    //容错，降级
    private Class tolerant;
    //服务地址
    private String address;
    //方法参数列表
    private Object[] args;
    //消息体
    private Body body;
    //请求ID
    private String requestId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Body implements Serializable {
        private static final long serialVersionUID = 1L;
        //消息值
        private Object body;
        //异常值
        private Throwable throwable;
    }
}
