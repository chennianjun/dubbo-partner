package com.base.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.base.dubbo.service.DemoService;
import com.base.dubbo.service.impl.MyGenericService;

public class Provider {
    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"provider.xml"});
//        context.start();
//        System.out.println("启动成功...");
//        System.in.read(); // 按任意键退出
        GenericService genericService = new MyGenericService();
        ServiceConfig<GenericService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(DemoService.class);
        serviceConfig.setRef(genericService);
        serviceConfig.setApplication(new ApplicationConfig("genericServiceProvider"));
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("127.0.0.1:2181");
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.export();
        System.out.println("启动成功...");
        System.in.read();
    }
}