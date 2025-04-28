package com.renjia.rpc.loadBalancer;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

public interface LoadBalancer {

    default Object choice(List<String> chaices){
        if (chaices == null) return null;
        else if (chaices.size() != 1) return doChoice(chaices);
        else return chaices.get(0);
    }

    Object doChoice(List<String> chaices);

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
