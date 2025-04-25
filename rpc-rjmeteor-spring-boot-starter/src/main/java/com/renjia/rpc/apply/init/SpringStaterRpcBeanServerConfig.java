package com.renjia.rpc.apply.init;

import com.renjia.rpc.core.Config;
import com.renjia.rpc.core.ContextInitConfig;
import com.renjia.rpc.registrationCenter.Register;
import com.renjia.rpc.serializer.Serializer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Data
@ConfigurationProperties(prefix = "rpc")
public class SpringStaterRpcBeanServerConfig {

    private ContextInitConfig.Registe registe;

}
