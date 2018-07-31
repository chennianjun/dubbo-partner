package com.base.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.base.dubbo.service.DemoService;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
//        context.start();
//        GenericService genericService = (GenericService)context.getBean("demoService");
//        Object sayHello = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"generic"});
//        System.out.println(sayHello);

        //使用java配置
        // 引用远程服务
        // 该实例很重量，里面封装了所有与注册中心及服务提供方连接，请缓存
        ReferenceConfig<GenericService> serviceReferenceConfig = new ReferenceConfig<>();
        //配置应用相关信息
        ApplicationConfig applicationConfig = new ApplicationConfig("genericName");
        serviceReferenceConfig.setApplication(applicationConfig);
        serviceReferenceConfig.setInterface(DemoService.class);
        //配置注册信息
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress("127.0.0.1:2181");
        registryConfig.setProtocol("zookeeper");
        serviceReferenceConfig.setRegistry(registryConfig);
        // 声明为泛化接口
        serviceReferenceConfig.setGeneric(true);
        // 用com.alibaba.dubbo.rpc.service.GenericService可以替代所有接口引用
        GenericService genericService = serviceReferenceConfig.get();
        // 基本类型以及Date,List,Map等不需要转换，直接调用
        Object sayHello = genericService.$invoke("sayHello", new String[]{"java.lang.String"}, new Object[]{"generic"});
        System.out.println(sayHello);

        // 用Map表示POJO参数，如果返回值为POJO也将自动转成Map
        Map<String, Object> person = new HashMap<>();
        person.put("id", "007");
        person.put("name", "zhangsan");
        // 如果返回POJO将自动转成Map
        Object findByPerson = genericService.$invoke("findByPerson", new String[]{"com.base.dubbo.domain.Person"}, new Object[]{person});
        System.out.println(findByPerson);
    }
}