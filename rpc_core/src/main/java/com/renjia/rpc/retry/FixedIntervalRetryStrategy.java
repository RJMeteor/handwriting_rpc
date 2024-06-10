package com.renjia.rpc.retry;

import com.github.rholder.retry.*;
import com.renjia.rpc.protocol.RpcProtocol;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class FixedIntervalRetryStrategy implements Retry {

    @SneakyThrows
    @Override
    public RpcProtocol doRetry(Callable<RpcProtocol> callable) {
        Retryer<RpcProtocol> retryer = RetryerBuilder.<RpcProtocol>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                //设置根据结果重试
                .retryIfResult(protocol -> protocol.getBody().getThrowable() != null)
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println("重试次数 {"+attempt.getAttemptNumber()+"}");
                    }
                })
                .build();
        return retryer.call(callable);
    }

}
