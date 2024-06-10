package com.renjia.rpc;

import com.renjia.rpc.anno.ConsumerScanPackage;
import com.renjia.rpc.anno.ProducerScanPackage;
import com.renjia.rpc.core.VertxServer;
import javassist.CannotCompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@ProducerScanPackage(packages = "com.renjia.rpc.controller")
@ConsumerScanPackage(packages = "com.renjia.rpc.fetch")
public class RpcStarter<T> {


    public static void main(String[] args) {

        VertxServer vertxServer = new VertxServer();
        vertxServer.start();
    }


}
