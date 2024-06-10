package com.renjia.rpc.retry;

import com.renjia.rpc.protocol.RpcProtocol;
import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * 重试策略
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @learn <a href="https://codefather.cn">鱼皮的编程宝典</a>
 * @from <a href="https://yupi.icu">编程导航学习圈</a>
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
