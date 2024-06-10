package com.renjia.rpc.protocol;

import cn.hutool.core.util.IdUtil;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.anno.RpcFetch;
import com.renjia.rpc.core.VertxHttpClient;
import com.renjia.rpc.factory.LoadResourceFactory;
import com.renjia.rpc.retry.Retry;
import com.renjia.rpc.tolerant.FaultTolerant;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RequestInvocationHandler implements InvocationHandler {

    private Class target;

    public RequestInvocationHandler(Class target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestInfo requestInfo = method.getAnnotation(RequestInfo.class);
        RpcFetch rpcFetch = Class.forName(target.getName()).getAnnotation(RpcFetch.class);
        //构建基本请求
        RpcProtocol protocol = RpcProtocol.builder()
                .serverName(rpcFetch.serverName())
                .loadBalancer(rpcFetch.loadBalancer())
                .retry(rpcFetch.retry())
                .tolerant(rpcFetch.tolerant())
                .args(args)
                .requestId(IdUtil.getSnowflakeNextIdStr())
                .requestUrl(requestInfo.requesetUrl())
                .requestType(requestInfo.requestType())
                .build();

        Retry retry = LoadResourceFactory.load(Retry.class, protocol.getRetry().getType());


        RpcProtocol rpcProtocol = null;
        try {
            rpcProtocol = retry.doRetry(() -> VertxHttpClient.doRequest(protocol));
        } catch (Exception e) {
            FaultTolerant faultTolerant = (FaultTolerant) protocol.getTolerant().newInstance();
            return faultTolerant.tolerant();
        }

        return rpcProtocol.getBody().getBody();
    }
}
