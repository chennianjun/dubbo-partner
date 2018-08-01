package com.base.dubbo.service.impl;

import com.alibaba.dubbo.rpc.service.GenericException;
import com.alibaba.dubbo.rpc.service.GenericService;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MyGenericService implements GenericService {
    @Override
    public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
        if ("sayHello".equals(method)) {
            return "welcome " + args[0];
        }
        if ("findByPerson".equals(method)) {
            try {
                Class<?> aClass = Class.forName(parameterTypes[0]);
                Object instance = aClass.newInstance();
                Field[] declaredFields = instance.getClass().getDeclaredFields();
                HashMap<String, Object> hashMap = (HashMap<String, Object>) args[0];
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    field.set(instance, hashMap.get(field.getName()));
                }
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "NOT FOUND";
    }
}
