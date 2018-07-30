package com.base.dubbo.service.impl;

import com.base.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }
}