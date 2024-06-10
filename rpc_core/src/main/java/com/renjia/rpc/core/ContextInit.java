package com.renjia.rpc.core;

import com.renjia.rpc.registrationCenter.Register;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ContextInit {

    protected ExecutorService service = Executors.newFixedThreadPool(1);


    //注册实例
    protected Register register;

    protected Vertx vertx;

    protected Router router;


    //初始化容器
    protected abstract void init();

    //启动容器
    protected abstract void start();

    //启动注册器
    protected abstract void startRegister();

    //初始化服务提供者
    protected abstract void serviceProvisionInit();

    //是否是Vertx web 环境
    protected abstract Boolean isVertxEnvironment();


}
