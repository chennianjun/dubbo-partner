package com.base.dubbo;

import com.base.dubbo.domain.Person;
import com.base.dubbo.service.DemoService;
import com.base.dubbo.service.Notify;
import com.base.dubbo.service.impl.NotifyImpl;
import org.junit.Assert;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        context.start();
        DemoService demoService = (DemoService)context.getBean("demoService"); // 获取远程服务代理
        NotifyImpl notify= (NotifyImpl) context.getBean("notify");
        String requestId="2";
        Person person = demoService.get(requestId);// 执行远程方法
        Assert.assertEquals(null,person);
        //for Test：只是用来说明callback正常被调用，业务具体实现自行决定.
        for (int i = 0; i < 10; i++) {
            if (!notify.ret.containsKey(requestId)) {
                Thread.sleep(200);
            } else {
                break;
            }
        }
        Assert.assertEquals(requestId, notify.ret.get(requestId).getId());

        Person personEx = demoService.get("-1");// 执行远程方法
        Assert.assertEquals(null,personEx);
    }
}