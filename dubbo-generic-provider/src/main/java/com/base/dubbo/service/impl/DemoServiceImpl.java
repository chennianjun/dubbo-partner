package com.base.dubbo.service.impl;

import com.base.dubbo.domain.Person;
import com.base.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }

    @Override
    public Person findByPerson(Person person) {
        System.out.println(person);
        return person;
    }
}