package com.base.dubbo.service.impl;

import com.base.dubbo.service.DemoService;

import java.util.ArrayList;
import java.util.List;

public class DemoServiceImpl2 implements DemoService {
    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }

    @Override
    public List<String> getList() {
        List<String> stringList=new ArrayList<>();
        stringList.add("merge-list-3");
        stringList.add("merge-list-4");
        return stringList;
    }
}