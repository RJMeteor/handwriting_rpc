package com.renjia.consumer.fetch;

import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.anno.RpcFetch;
import com.renjia.rpc.retry.Retry;

@RpcFetch(serverName = "producer",retry = Retry.Type.FIXED)
public interface UserFetch {

    @RequestInfo(requesetUrl = "/login", requestType = RequestInfo.RequestType.POST)
    public String login(String name);
}
