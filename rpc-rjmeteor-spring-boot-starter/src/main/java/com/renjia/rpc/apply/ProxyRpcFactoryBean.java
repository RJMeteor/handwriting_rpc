package com.renjia.rpc.apply;

import com.renjia.rpc.protocol.RequestInvocationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

public class ProxyRpcFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {
    private Class<T> mapperInterface;
    private ApplicationContext applicationContext;

    public ProxyRpcFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(this.mapperInterface.getClassLoader(), new Class[]{this.mapperInterface}, new RequestInvocationHandler(this.mapperInterface, applicationContext.getBean(RequestHttpClient.class)));
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
