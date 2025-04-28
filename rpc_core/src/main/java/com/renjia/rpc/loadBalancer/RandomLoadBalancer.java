package com.renjia.rpc.loadBalancer;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

/**
 * 随机
 */
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Object doChoice(List<String> chaices) {
        String s = chaices.get(RandomUtil.randomInt(0, chaices.size()));
        String substring = s.substring(0, s.lastIndexOf(":"));
        return substring;
    }
}
