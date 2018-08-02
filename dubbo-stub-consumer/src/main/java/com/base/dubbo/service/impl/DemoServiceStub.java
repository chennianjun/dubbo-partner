package com.base.dubbo.service.impl;

import com.base.dubbo.domain.Person;
import com.base.dubbo.service.DemoService;

public class DemoServiceStub implements DemoService {

    private DemoService demoService;

    public DemoServiceStub(DemoService demoService) {
        this.demoService = demoService;
    }

    @Override
    public Person get(String id) {
        Person person = null;
        try {
            if (Integer.parseInt(id) > 0) {
                person = this.demoService.get(id);
            } else {
                person = new Person();
                person.setName("系统用户");
            }
        } catch (Exception e) {
            person = new Person();
            person.setName("异常用户");
        }
        return person;
    }
}
