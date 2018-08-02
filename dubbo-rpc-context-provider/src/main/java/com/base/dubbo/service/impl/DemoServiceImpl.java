package com.base.dubbo.service.impl;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.RpcContext;
import com.base.dubbo.service.DemoService;

import java.util.Map;

public class DemoServiceImpl implements DemoService {
    public String sayHello(String name) {

//        Map<String, String> attachments = RpcContext.getContext().getAttachments();
//        String attch= attachments.get("attch");
//        System.out.println("附件信息："+attch);

        String attachment = RpcContext.getContext().getAttachment("attch");
        String attachment1 = RpcContext.getContext().getAttachment("a");
        System.out.println(attachment+":"+attachment1);
        // 本端是否为提供端，这里会返回true
        boolean providerSide = RpcContext.getContext().isProviderSide();
        // 获取调用方IP地址
        String remoteHost = RpcContext.getContext().getRemoteHost();

        String application = RpcContext.getContext().getUrl().getParameter("application");
        URL url = RpcContext.getContext().getUrl().addParameter("zhangsan", "HaHa");
        System.out.println(url.toFullString());
        RpcContext.getContext().set("zhangsan", "lala");
        System.out.println(name+",providerSide:"+providerSide+",remoteHost:"+remoteHost+",application:"+application);
        return "Hello " + name;
    }
}