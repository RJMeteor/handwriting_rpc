package com.renjia.rpc.loadBalancer;

import com.renjia.rpc.RpcStarter;

import java.util.*;

/**
 * 空闲
 */
public class FreeLoadBalancer implements LoadBalancer {
    @Override
    public Object doChoice(List<String> chaices) {
        ArrayList<String> strings = new ArrayList<>();
        for (String chaice : chaices) {
            strings.add(chaice);
        }
        strings.sort(new SortComp());
        String freeFiled = strings.get(0);
        return freeFiled.substring(0, freeFiled.lastIndexOf(":"));
    }

    static class SortComp implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int o1Limit = o1.lastIndexOf(":");
            int o2Limit = o2.lastIndexOf(":");
            Integer o1Value = Integer.parseInt(o1.substring(o1Limit + 1));
            Integer o2Value = Integer.parseInt(o2.substring(o2Limit + 1));
            return o1Value - o2Value;
        }
    }
}
