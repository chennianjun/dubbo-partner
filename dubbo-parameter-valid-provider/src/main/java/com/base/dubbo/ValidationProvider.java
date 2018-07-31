package com.base.dubbo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo
 * @create_date:2018/1/23 10:48
 * @author:Subtimental
 * @description:TODO
 */
public class ValidationProvider {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
        context.start();
        System.out.println("校验dubbo服务启动成功...");
        System.in.read();
    }
}
