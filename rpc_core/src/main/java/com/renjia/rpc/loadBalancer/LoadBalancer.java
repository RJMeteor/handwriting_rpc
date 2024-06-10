package com.renjia.rpc.loadBalancer;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

public interface LoadBalancer {

    Object choice(List<String> chaices);

    @Getter
    public enum Type implements Serializable {
        FREE("free"), POLL("poll"), RANDOM("random");
        private static final long serialVersionUID = 1L;
        String type;

        Type(String type) {
            this.type = type;
        }
    }
}
