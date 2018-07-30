package com.base.dubbo;

import com.base.dubbo.service.AddService;
import com.base.dubbo.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("multi-registry.xml");
        context.start();
        DemoService demoService = context.getBean(DemoService.class);
        String sayHello = demoService.sayHello("multi registry");
        System.out.println(sayHello);

        AddService addService = context.getBean(AddService.class);
        int add = addService.add(3, 5);
        System.out.println(add);
    }
}
