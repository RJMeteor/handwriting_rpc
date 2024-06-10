package com.renjia.rpc.util.scan;

import com.renjia.rpc.anno.ConsumerScanPackage;
import com.renjia.rpc.anno.ProducerScanPackage;
import com.renjia.rpc.core.StorageRegisterInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PackgeScan {

    private static HashMap<Class, Strategy> strategyMapping = new HashMap();

    static {
        PackgeScan.regitsStrategy(ConsumerScanPackage.class, new ConsumerScanAnnoStrategy());
        PackgeScan.regitsStrategy(ProducerScanPackage.class, new ProducerScanAnnoStrategy());
    }

    public static void regitsStrategy(Class annoClass, Strategy strategy) {
        strategyMapping.put(annoClass, strategy);
    }

    public static ArrayList<Class> scan(Class annoClass) {
        Strategy strategy = strategyMapping.get(annoClass);
        if (strategy == null) return null;
        Boolean classSuport = strategy.isSuport(annoClass);
        if (classSuport) return strategy.scan(annoClass);
        return strategy.scanByConfig();
    }

}
