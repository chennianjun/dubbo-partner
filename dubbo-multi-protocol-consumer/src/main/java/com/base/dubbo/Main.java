package com.base.dubbo;

import com.base.dubbo.service.AddService;
import com.base.dubbo.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("dubbo.xml");
        context.start();
//        DemoService demoService = (DemoService) context.getBean("demoService");
//        String sayHello = demoService.sayHello("Multi Protocol");
//        System.out.println("demoService:"+sayHello);

        DemoService demoService1 =  context.getBean(DemoService.class);
        String sayHello1 = demoService1.sayHello("Multi Protocol");
        System.out.println("demoService1:"+sayHello1);
        AddService addService = context.getBean(AddService.class);
        int add = addService.add(2, 4);
        System.out.println(add);
    }
}
