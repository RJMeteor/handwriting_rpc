package com.renjia.rpc.registrationCenter;

import com.renjia.rpc.core.ContextInit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

public interface Register {

    @Getter
    enum Type implements Serializable {
        ETCD("etcd");
        private static final long serialVersionUID = 1L;
        String type;

        Type(String type) {
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    void init();

    List<String> discovery(String key);

    //注册
    void register(String key, String value);

    //解绑
    void unRegister(String key);

    //心跳检测
    void heartbeat(String key);

    //监听是否下线
    void monitor(String key);

    void destroy();

}
