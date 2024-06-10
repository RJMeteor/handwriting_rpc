package com.renjia.producer.controller;

import cn.hutool.core.util.IdUtil;
import com.renjia.rpc.anno.ExposeParam;
import com.renjia.rpc.anno.ExposePoint;
import com.renjia.rpc.anno.RequestInfo;
import com.renjia.rpc.anno.RpcExposure;

@RpcExposure
public class UserController {

    @ExposePoint(pointUrl = "/login",pointType = RequestInfo.RequestType.POST)
    public String login(@ExposeParam(name = "name") String name) {

//        return IdUtil.fastSimpleUUID();

        return "producer_1";
    }
}
