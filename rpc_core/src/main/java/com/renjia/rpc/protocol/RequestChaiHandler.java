package com.renjia.rpc.protocol;

public interface RequestChaiHandler<T> {
    void handle(T context);

    boolean isAccordWith(T context);
}
