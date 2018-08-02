package com.base.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import com.base.dubbo.service.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
        RpcContext.getContext().setAttachment("attch", "逗你玩");
        RpcContext.getContext().setAttachment("a","b");
        String hello = demoService.sayHello("rpc context"); // 执行远程方法
        RpcContext rpcContext = RpcContext.getContext();
        boolean consumerSide = rpcContext.isConsumerSide();
        String remoteHost = rpcContext.getRemoteHost();
        String parameter = rpcContext.getUrl().toFullString();
        System.out.println(consumerSide+":"+remoteHost+":"+parameter);
        System.out.println(hello); // 显示调用结果
    }
}