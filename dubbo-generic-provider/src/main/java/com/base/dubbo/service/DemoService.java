package com.base.dubbo.service;

import com.base.dubbo.domain.Person;

public interface DemoService {
    String sayHello(String name);

    Person findByPerson(Person person);
}