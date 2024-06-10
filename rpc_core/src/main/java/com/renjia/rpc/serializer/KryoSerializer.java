package com.renjia.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Kryo
 */
public class KryoSerializer implements Serializer {
    @SneakyThrows
    public byte[] serialize(RpcProtocol protocol) {
        Kryo kryo = new Kryo();
        kryo.register(RpcProtocol.class);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, protocol);//写入null时会报错
        output.close();
        return bos.toByteArray();
    }

    @SneakyThrows
    public RpcProtocol deserialize(byte[] data) {
        Kryo kryo = new Kryo();
        kryo.register(RpcProtocol.class);
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        Input input = new Input(bis);
        RpcProtocol userDto = kryo.readObject(input, RpcProtocol.class);//读出null时会报错
        input.close();
        return userDto;
    }
}
