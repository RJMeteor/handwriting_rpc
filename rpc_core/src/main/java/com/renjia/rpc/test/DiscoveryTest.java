package com.renjia.rpc.test;

public class DiscoveryTest {
    public static void main(String[] args) {
        String endpoints = "http://localhost:2379";
        Discovery ser = new Discovery(endpoints);
        ser.watchService("/web/");
        ser.watchService("/grpc/");
        while (true) {

        }
    }
}
