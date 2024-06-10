package com.renjia.rpc.test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Response;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Discovery {
    private Client client;
    private String endpoints;
    private final Object lock = new Object();
    private HashMap<String, String> serverList = new HashMap<String, String>();

    /**
     * 发现服务类信息初始化
     * @param endpoints：监听端点，包含ip和端口，如："http://localhost:2379“，多个端点则使用逗号分割， 比如：”http://localhost:2379,http://192.168.2.1:2330“
     */
    public Discovery(String endpoints) {
        this.endpoints = endpoints;
        newServiceDiscovery();
    }

    public Client newServiceDiscovery() {
        if (null == client) {
            synchronized (lock) {
                if (null == client) {
                    client = Client.builder().endpoints(endpoints.split(",")).build();
                }
            }
        }

        return client;
    }

    public void watchService(String prefixAddress) {
        //请求当前前缀
        CompletableFuture<GetResponse> getResponseCompletableFuture =
                client.getKVClient().get(ByteSequence.from(prefixAddress,
                        UTF_8),
                        GetOption.newBuilder().withPrefix(ByteSequence.from(prefixAddress, UTF_8)).build());

        try {
            //获取当前前缀下的服务并存储
            List<KeyValue> kvs = getResponseCompletableFuture.get().getKvs();
            for (KeyValue kv : kvs) {
                setServerList(kv.getKey().toString(UTF_8), kv.getValue().toString(UTF_8));
            }

            //创建线程监听前缀
            new Thread(new Runnable() {

                @Override
                public void run() {
                    watcher(prefixAddress);
                }
            }).start();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void watcher(String prefixAddress) {
        Watcher watcher;

        System.out.println("watching prefix:" + prefixAddress);
        WatchOption watchOpts = WatchOption.newBuilder().withPrefix(ByteSequence.from(prefixAddress,
                UTF_8)).build();

        //实例化一个监听对象，当监听的key发生变化时会被调用
        Watch.Listener listener = Watch.listener(watchResponse -> {
            watchResponse.getEvents().forEach(watchEvent -> {
                WatchEvent.EventType eventType = watchEvent.getEventType();
                KeyValue keyValue = watchEvent.getKeyValue();
                System.out.println("type="+eventType+",key="+keyValue.getKey().toString(UTF_8)+",value="+keyValue.getValue().toString(UTF_8));

                switch (eventType) {
                    case PUT:  //修改或者新增
                        setServerList(keyValue.getKey().toString(UTF_8), keyValue.getValue().toString(UTF_8));
                        break;
                    case DELETE: //删除
                        delServerList(keyValue.getKey().toString(UTF_8), keyValue.getValue().toString(UTF_8));
                        break;
                }
            });
        });

        client.getWatchClient().watch(ByteSequence.from(prefixAddress, UTF_8), watchOpts,
                listener);
    }

    private void setServerList(String key, String value) {
        synchronized (lock) {
            serverList.put(key, value);
            System.out.println("put key:" + key + ",value:" + value);
        }
    }

    private void delServerList(String key, String value) {
        synchronized (lock) {
            serverList.remove(key);
            System.out.println("del key:" + key);
        }
    }

    public void close() {
        client.close();
        client = null;
    }
}
