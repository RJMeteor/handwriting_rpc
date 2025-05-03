package com.renjia.rpc.core;

import com.renjia.rpc.registrationCenter.Register;
import com.renjia.rpc.serializer.Serializer;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
public class ContextInitConfig {
    //服务公开ip
    private String host;

    //服务公开端口
    private Integer port = 8023;

    //服务提供者扫描包
    private String provisionScanPackge;

    //远程调度者扫描包
    private String remoteScanPackge;

    private Registe registe;

    {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.host = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

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
