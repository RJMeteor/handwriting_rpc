package com.renjia.rpc.util.scan;

import com.renjia.rpc.anno.ConsumerScanPackage;
import com.renjia.rpc.core.StorageRegisterInfo;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class ConsumerScanAnnoStrategy implements Strategy {
    @SneakyThrows
    @Override
    public ArrayList<Class> scan(Class scanPackgeClass) {
        ConsumerScanPackage annotation = (ConsumerScanPackage) Class.forName(System.getProperty("sun.java.command")).getAnnotation(scanPackgeClass);
        ArrayList<Class> classes = new ArrayList<>();
        for (String aPackage : annotation.packages()) {
            String packagePath = aPackage.replace('.', '/');
            classes.addAll(this.doScan(packagePath));
        }
        return classes;
    }

    @SneakyThrows
    @Override
    public Boolean isSuport(Class scanPackage) {
        return Class.forName(System.getProperty("sun.java.command")).getAnnotation(scanPackage) != null ? true : false;
    }

    @Override
    public ArrayList<Class> scanByConfig() {
        String remoteScanPackge = StorageRegisterInfo.initConfig.getRemoteScanPackge();
        if (remoteScanPackge == null) return null;
        ArrayList<Class> classes = new ArrayList<>();
        for (String oreginPack : remoteScanPackge.split(";")) {
            String packagePath = oreginPack.replace('.', '/');
            classes.addAll(this.doScan(packagePath));
        }
        return classes;
    }
}
