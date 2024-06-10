package com.renjia.rpc.core;

public class VertxServer extends StorageRegisterInfo {

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected void serviceProvisionInit() {
        super.serviceProvisionInit();
    }

    @Override
    protected ContextInitConfig initContextConfig() {
        return super.initContextConfig();
    }

    @Override
    protected Boolean isVertxEnvironment() {
        return true;
    }


}
