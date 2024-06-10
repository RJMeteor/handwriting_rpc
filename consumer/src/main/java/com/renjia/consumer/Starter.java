package com.renjia.consumer;

import com.renjia.rpc.core.VertxServer;

public class Starter {
    public static void main(String[] args) {
        VertxServer vertxServer = new VertxServer();
        vertxServer.start();
    }
}
