package com.base.dubbo.service.impl;

import com.base.dubbo.domain.Person;
import com.base.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {

    @Override
    public Person get(String id) {
        if (Integer.parseInt(id)<0){
            throw new RuntimeException("id不合法");
        }
        return new Person(id, "charles`son", 4);
    }
}