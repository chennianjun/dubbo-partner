package com.base.dubbo.service;

import com.base.dubbo.domain.Person;

public interface DemoService {
    Person get(String id);
}