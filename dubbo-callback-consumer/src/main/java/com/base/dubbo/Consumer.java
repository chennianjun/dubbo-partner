package com.base.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.base.dubbo.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Future;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"consumer.xml"});
        context.start();
        DemoService demoService = (DemoService) context.getBean("demoService"); // 获取远程服务代理
        String hello = demoService.sayHello("async"); // 执行远程方法
        System.out.println(hello); // 立即返回null
        // 拿到调用的Future引用，当结果返回后，会被通知和设置到此Future
        Future<String> stringFuture = RpcContext.getContext().getFuture();
        //打印返回结果
        System.out.println(stringFuture.get());

        demoService.hello();

        demoService.addListener("callBackLister", (msg) -> System.out.println("callback1:" + msg));
    }
}