package com.base.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext classPathXmlApplicationContext=new ClassPathXmlApplicationContext("multi-registry.xml");
        classPathXmlApplicationContext.start();
        System.out.println("启动成功...");
        System.in.read();
    }
}
