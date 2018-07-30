package com.base.dubbo.service.impl;

import com.base.dubbo.service.AddService;

public class AddServiceImpl implements AddService {

    @Override
    public int add(int a, int b) {
        System.out.println("传入的数值为" + a + ",另一个" + b);
        return a+b;
    }
}
