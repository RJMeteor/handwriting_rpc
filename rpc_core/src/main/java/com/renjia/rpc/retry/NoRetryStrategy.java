package com.renjia.rpc.retry;

import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;


@Slf4j
public class NoRetryStrategy implements Retry {

    @SneakyThrows
    @Override
    public RpcProtocol doRetry(Callable<RpcProtocol> callable) {
        return callable.call();
    }

}
