package com.renjia.rpc.test;

public class RegisterTest {
    public static void main(String[] args) {
        Register register = new Register("http://localhost:2379");
        String key = "/web/node0";
        String value = "localhost:7999";
//        try {
//            register.put(key, value);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            register.putWithLease(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
