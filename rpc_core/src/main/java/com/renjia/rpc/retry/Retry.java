package com.renjia.rpc.retry;

import com.renjia.rpc.protocol.RpcProtocol;
import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * 重试策略
 */
public interface Retry {

    RpcProtocol doRetry(Callable<RpcProtocol> callable);

    @Getter
    public enum Type implements Serializable {
        FIXED("fixed"), NO("no");
        private static final long serialVersionUID = 1L;
        String type;

        Type(String type) {
            this.type = type;
        }
    }
}
