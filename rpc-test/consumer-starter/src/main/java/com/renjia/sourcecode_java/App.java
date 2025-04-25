package com.renjia.sourcecode_java;

import com.renjia.rpc.apply.EnableRPCAutoServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableRPCAutoServer(scanPackage = "com.renjia.sourcecode_java.fetch")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
