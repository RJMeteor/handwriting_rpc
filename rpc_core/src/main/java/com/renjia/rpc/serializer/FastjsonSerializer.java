package com.renjia.rpc.serializer;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * json
 */
public class FastjsonSerializer implements Serializer {
    @SneakyThrows
    public byte[] serialize(RpcProtocol protocol) {
        byte[] bytes = JSON.toJSONString(protocol).getBytes();
        return bytes;
    }

    @SneakyThrows
    public RpcProtocol deserialize(byte[] data) {
        RpcProtocol rpcProtocol = JSON.parseObject(new String(data), RpcProtocol.class);
        return (RpcProtocol) rpcProtocol;
    }
}
