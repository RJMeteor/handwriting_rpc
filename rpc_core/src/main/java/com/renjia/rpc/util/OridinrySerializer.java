package com.renjia.rpc.util;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.renjia.rpc.protocol.RpcProtocol;
import com.renjia.rpc.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian
 */
public class OridinrySerializer {
    public byte[] serialize(Object o) throws IOException {
        if (o == null) return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(bos);
        output.writeObject(o);
        output.flush();
        return bos.toByteArray();
    }

    public Object deserialize(byte[] data) throws IOException {
        if (data == null || data.length == 0) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        HessianInput input = new HessianInput(bis);
        return input.readObject();
    }
}
