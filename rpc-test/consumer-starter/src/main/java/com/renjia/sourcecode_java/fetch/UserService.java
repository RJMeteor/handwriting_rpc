package com.renjia.sourcecode_java.fetch;

import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.anno.RpcFetch;
import com.renjia.rpc.retry.Retry;

@RpcFetch(serverName = "producer", retry = Retry.Type.FIXED)
public interface UserService {

    @RequestInfo(requesetUrl = "/renjia/name", requestType = RequestInfo.RequestType.GET)
    public String login();

    @RequestInfo(requesetUrl = "/renjia/get", requestType = RequestInfo.RequestType.GET)
    public String get(String userName, String age);

    @RequestInfo(requesetUrl = "/renjia/go", requestType = RequestInfo.RequestType.GET)
    public User go(String userName, Integer age);

    @RequestInfo(requesetUrl = "/renjia/post", requestType = RequestInfo.RequestType.POST)
    public User post(User user, User user1);
}