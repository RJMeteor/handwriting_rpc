package com.renjia.rpc.tolerant;

public class DefaultValueFaultTolerant implements FaultTolerant {
    @Override
    public Object tolerant() {
        return "后端服务器错误，请重试";
    }
}
