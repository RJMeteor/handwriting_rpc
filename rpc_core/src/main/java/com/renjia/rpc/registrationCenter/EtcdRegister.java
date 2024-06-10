package com.renjia.rpc.registrationCenter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import com.renjia.rpc.core.StorageRegisterInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.Data;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class EtcdRegister implements Register {

    private String PREFIX = "/rpc/rj/";

    //正在监听的服务
    private HashSet<String> watchServices = new HashSet<>();

    //注册服务
    private HashSet<String> registerServices = new HashSet<>();


    private Client client;

    private KV kvClient;

    {
        this.PREFIX = StorageRegisterInfo.initConfig.getRegiste().getRegistePrefix();
    }

    @Override
    public void init() {
        client = Client.builder()
                .endpoints(StorageRegisterInfo.initConfig.getRegiste().getEnpoint())
                .build();
        kvClient = client.getKVClient();
    }

    @Override
    public List<String> discovery(String key) {
        String searchPrefix = PREFIX;
        String serverId = PREFIX + key;
        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption)
                    .get()
                    .getKvs();
//            // 解析服务信息
            List<String> serviceMetaInfoList = keyValues.stream()
                    .filter(keyValue -> keyValue.getKey().toString(StandardCharsets.UTF_8) != serverId)
                    .map(keyValue -> {
                        String k = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        long version = keyValue.getVersion();
                        // 监听 key 的变化
                        monitor(k);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return value + ":" + version;
                    })
                    .collect(Collectors.toList());
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @SneakyThrows
    @Override
    public void register(String key, String value) {
        // 创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();
        // 创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String regisKey = PREFIX + key;
        ByteSequence k = ByteSequence.from(regisKey, StandardCharsets.UTF_8);
        ByteSequence v = ByteSequence.from(value, StandardCharsets.UTF_8);
        // 将键值对与租约关联起来，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(k, v, putOption);
        heartbeat(regisKey);
        monitor(regisKey);
    }

    @Override
    public void unRegister(String key) {
        String regisKey = PREFIX + key;
        kvClient.delete(ByteSequence.from(regisKey, StandardCharsets.UTF_8));
        watchServices.remove(regisKey);
        registerServices.remove(regisKey);
    }

    @Override
    public void heartbeat(String key) {
        String regisKey = key;
        boolean add = registerServices.add(regisKey);
        if (!add) return;
        // 10 秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key
                for (String key : registerServices) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(regisKey, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        String[] split = key.split(PREFIX);
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        register(split[1], value);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void monitor(String key) {
        String regisKey = key;
        Watch watchClient = client.getWatchClient();
        if (watchServices.add(regisKey)) {
            watchClient.watch(ByteSequence.from(regisKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            watchServices.remove(regisKey);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }

    @SneakyThrows
    @Override
    public void destroy() {
        for (String registerService : registerServices) {
            kvClient.delete(ByteSequence.from(registerService, StandardCharsets.UTF_8)).get();
        }

        // 释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
