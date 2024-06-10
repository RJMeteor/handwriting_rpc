package com.renjia.rpc.core;

import cn.hutool.core.util.IdUtil;
import cn.hutool.setting.dialect.Props;
import com.renjia.rpc.anno.ConsumerScanPackage;
import com.renjia.rpc.anno.ExposePoint;
import com.renjia.rpc.anno.ProducerScanPackage;
import com.renjia.rpc.anno.RpcExposure;
import com.renjia.rpc.factory.LoadResourceFactory;
import com.renjia.rpc.registrationCenter.Register;
import com.renjia.rpc.serializer.Serializer;
import com.renjia.rpc.util.RouterInit;
import com.renjia.rpc.util.scan.ConsumerScanAnnoStrategy;
import com.renjia.rpc.util.scan.PackgeScan;
import com.renjia.rpc.util.scan.ProducerScanAnnoStrategy;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;

@Data
public abstract class StorageRegisterInfo extends ContextInit {

    //配置文件路径
    private String configFilePath = "rpc.config.properties";

    public static ContextInitConfig initConfig;


    public StorageRegisterInfo() {
        initConfig = initContextConfig();
    }

    @Override
    protected void startRegister() {
        Register register = LoadResourceFactory.load(Register.class, initConfig.getRegiste().getRegisterType().getType());
        this.register = register;
        register.init();
        if (initConfig.getRegiste().getEnable()) register.register(initConfig.getRegiste().getServerName(),
                initConfig.getHost() + ":" + initConfig.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(register::destroy));
    }

    @Override
    protected void init() {
        this.startRegister();
        this.serviceProvisionInit();
    }

    @SneakyThrows
    @Override
    protected void start() {
        if (!this.isVertxEnvironment()) {
            service.execute(() -> startRegister());
            return;
        }
        vertx = Vertx.vertx();
        router = Router.router(vertx);
        vertx.createHttpServer().requestHandler(router).listen(initConfig.getPort(), httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) service.execute(() -> init());
        });
    }

    @Override
    protected void serviceProvisionInit() {
        if (!this.isVertxEnvironment()) return;
        ArrayList<Class> scan = PackgeScan.scan(ProducerScanPackage.class);
        for (Class aClass : scan) {
            if (aClass.getAnnotation(RpcExposure.class) == null) continue;
            for (Method method : aClass.getMethods()) {
                ExposePoint point = method.getAnnotation(ExposePoint.class);
                if (point == null) continue;
                RouterInit.routerInit(router, aClass, method, point);
            }
        }
    }

    @SneakyThrows
    protected ContextInitConfig initContextConfig() {
        ContextInitConfig contextInitConfig = new Config();
        ContextInitConfig.Registe registe = contextInitConfig.getRegiste();
        Props props = new Props(configFilePath);
        contextInitConfig.setHost(props.getProperty("ri.rpc.host", contextInitConfig.getHost()));
        contextInitConfig.setPort(props.getInt("ri.rpc.port", contextInitConfig.getPort()));
        registe.setEnable(props.getBool("ri.rpc.registe.enable", registe.getEnable()));
        registe.setServerName(props.getProperty("ri.rpc.registe.serverName", IdUtil.getSnowflakeNextIdStr()));
        registe.setEnpoint(props.getProperty("rj.rpc.registe.enpoint", registe.getEnpoint()));
        registe.setRegistePrefix(props.getProperty("rj.rpc.registe.registePrefix", registe.getRegistePrefix()));
        registe.setRegisterType(props.getEnum(Register.Type.class, "rj.rpc.registe.registerType", registe.getRegisterType()));
        registe.setSerializerType(props.getEnum(Serializer.Type.class, "rj.rpc.registe.serializerType", registe.getSerializerType()));
        contextInitConfig.setProvisionScanPackge(props.getProperty("ri.rpc.provisionScanPackge", contextInitConfig.getProvisionScanPackge()));
        contextInitConfig.setRemoteScanPackge(props.getProperty("ri.rpc.remoteScanPackge", contextInitConfig.getRemoteScanPackge()));
        return contextInitConfig;
    }

}
