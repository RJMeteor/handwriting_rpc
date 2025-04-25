package com.renjia.consumer.controller;

import com.renjia.consumer.fetch.UserFetch;
import com.renjia.rpc.anno.ExposeParam;
import com.renjia.rpc.anno.ExposePoint;
import com.renjia.rpc.anno.RpcExposure;

@RpcExposure
public class UserController {

    @RpcExposure
    private UserFetch userFetch;

    @ExposePoint(pointUrl = "/login")
    public String login(@ExposeParam(name = "name") String name) {
        return userFetch.login(name);
    }


//    @ExposePoint(pointUrl = "/login", pointType = RequestInfo.RequestType.DELETE)
//    public String logindel(@ExposeParam(name = "name") String name) {
//        System.out.println(name);
//        return userFetch.login();
//    }
//
//    @ExposePoint(pointUrl = "/login", pointType = RequestInfo.RequestType.PUT)
//    public String loginput(@ExposeParam(name = "name") String name) {
//        System.out.println(name);
//        return userFetch.login();
//    }
//
//    @ExposePoint(pointUrl = "/login", pointType = RequestInfo.RequestType.POST)
//    public String loginpost(@ExposeParam(name = "name") String name) {
//        System.out.println(name+"+====");
//        return userFetch.login();
//    }
}
