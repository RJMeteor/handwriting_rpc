package com.renjia.rpc.apply.init;


import com.renjia.rpc.core.Config;
import com.renjia.rpc.core.ContextInitConfig;
import com.renjia.rpc.core.StorageRegisterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

public class InitRpcResource extends StorageRegisterInfo implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {
    private SpringStaterRpcBeanServerConfig config;
    private Environment environment;

    public InitRpcResource(SpringStaterRpcBeanServerConfig config) {
        this.config = config;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    protected Boolean isVertxEnvironment() {
        return false;
    }

    @Override
    protected ContextInitConfig initContextConfig() {
        ContextInitConfig contextInitConfig = new Config();
        contextInitConfig.setPort(environment.getProperty("server.port", Integer.class));
        contextInitConfig.setRegiste(this.config.getRegiste());
        return contextInitConfig;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationStartedEvent) {
        this.start();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
