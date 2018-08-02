package com.base.dubbo.service;

public interface DemoService {
    String sayHello(String name);

    void hello();

    void addListener(String key, CallbackListener callbackListener);
}