package com.base.dubbo;

import com.base.dubbo.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService2"); // 获取远程服务代理
        List<String> serviceList = demoService.getList();
        for (String s:serviceList){
            System.out.println(s);
        }
        String hello = demoService.sayHello("direct"); // 执行远程方法
        System.out.println(hello); // 显示调用结果
    }
}