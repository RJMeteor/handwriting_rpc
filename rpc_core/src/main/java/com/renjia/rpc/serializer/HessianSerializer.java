package com.renjia.rpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian
 */
public class HessianSerializer implements Serializer {
    @SneakyThrows
    public byte[] serialize(RpcProtocol protocol) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(bos);
        output.writeObject(protocol);
        output.flush();
        return bos.toByteArray();
    }

    @SneakyThrows
    public RpcProtocol deserialize(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        HessianInput input = new HessianInput(bis);
        return (RpcProtocol) input.readObject();
    }
}
