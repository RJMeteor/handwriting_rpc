package com.renjia.rpc.serializer;

import com.renjia.rpc.protocol.RpcProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Serializable;

public interface Serializer {
    @SneakyThrows
    byte[] serialize(RpcProtocol protocol);

    @SneakyThrows
    RpcProtocol deserialize(byte[] data);

    @Getter
    public enum Type implements Serializable {
        FASTJSON("json"), HESSIAN("hessian"), KRYO("kryo");
        private static final long serialVersionUID = 1L;
        String type;

        Type(String type) {
            this.type = type;
        }
    }
}
