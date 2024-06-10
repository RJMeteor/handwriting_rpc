package com.renjia.rpc.util.scan;

import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public interface Strategy {

    ArrayList<Class> scan(Class scanPackgeClass);

    ArrayList<Class> scanByConfig();

    Boolean isSuport(Class scanPackage);

    @SneakyThrows
    default ArrayList<Class> doScan(String scanPage) {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL resource = classLoader.findResource(scanPage);
        if (resource == null) return null;
        ArrayList<Class> classes = new ArrayList<>();
        Path rootPath = Paths.get(resource.toURI());
        Files.walk(rootPath)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    if (filePath.toString().endsWith(".class")) {
                        String className = filePath.toString()
                                .substring(rootPath.toString().length() + 1, filePath.toString().length() - 6)
                                .replace(File.separator, ".");
                        try {
                            String packagePath = scanPage.replace('/', '.');
                            Class<?> clazz = Class.forName(packagePath + "." + className);
                            int modifiers = clazz.getModifiers();
                            if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers)) {
                                classes.add(clazz);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        return classes;
    }
}
