package com.base.dubbo.service;

import java.util.List;

public interface DemoService {
    String sayHello(String name);
    List<String> getList();
}