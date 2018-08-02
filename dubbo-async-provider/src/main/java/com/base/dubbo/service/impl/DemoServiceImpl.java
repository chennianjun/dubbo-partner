package com.base.dubbo.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import com.base.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }

    @Override
    public void hello() {
        String fullString = RpcContext.getContext().getUrl().toFullString();
        System.out.println(fullString);
    }
}