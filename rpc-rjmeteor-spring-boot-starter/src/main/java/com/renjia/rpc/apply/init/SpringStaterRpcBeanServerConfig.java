package com.renjia.rpc.apply.init;

import com.renjia.rpc.registrationCenter.Register;
import com.renjia.rpc.serializer.Serializer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rpc")
public class SpringStaterRpcBeanServerConfig {

    private Registe registe = new Registe();

    @Data
    public static class Registe {
        //是否注册自己
        private Boolean enable = false;

        //etcd注册中心地址
        private String enpoint = "http://127.0.0.1:2379";

        //服务分组
        private String registePrefix = "/rj/rpc/";

        //注册服务名
        private String serverName;

        //服务注册类型
        private Register.Type registerType = Register.Type.ETCD;

        //消息序列化类型
        private Serializer.Type serializerType = Serializer.Type.FASTJSON;
    }
}
