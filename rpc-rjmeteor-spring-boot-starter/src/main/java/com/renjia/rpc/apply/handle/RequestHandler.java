package com.renjia.rpc.apply.handle;

import com.renjia.rpc.protocol.RpcProtocol;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.CompletableFuture;

public abstract class RequestHandler implements ApplicationContextAware, InitializingBean, ApplicationListener<ContextRefreshedEvent> {
    protected ApplicationContext applicationContext;
    protected static ChaiNode head;
    private static ChaiNode tail;


    public abstract void handle(RpcProtocol rpcProtocol, CompletableFuture<RpcProtocol> complet);

    public abstract Boolean isSuport(RpcProtocol rpcProtocol);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        applicationContext.publishEvent(head);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (head == null) {
            ChaiNode chaiNode = new ChaiNode(new Object());
            chaiNode.setHandler(this);
            tail = head = chaiNode;
        } else {
            ChaiNode build = new ChaiNode(head);
            build.setPre(tail);
            build.setHandler(this);
            tail.setNext(build);
            tail = build;
        }
    }


    public static class ChaiNode extends ApplicationEvent {
        private ChaiNode pre;
        private ChaiNode next;
        private RequestHandler handler;

        public ChaiNode(Object source) {
            super(source);
        }

        public void handle(RpcProtocol rpcProtocol, CompletableFuture<RpcProtocol> complet) {
            if (handler.isSuport(rpcProtocol)) {
                handler.handle(rpcProtocol, complet);
                return;
            } else if (next != null) next.handle(rpcProtocol, complet);
        }

        public ChaiNode getPre() {
            return pre;
        }

        public void setPre(ChaiNode pre) {
            this.pre = pre;
        }

        public ChaiNode getNext() {
            return next;
        }

        public void setNext(ChaiNode next) {
            this.next = next;
        }

        public RequestHandler getHandler() {
            return handler;
        }

        public void setHandler(RequestHandler handler) {
            this.handler = handler;
        }
    }
}
