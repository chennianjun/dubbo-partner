package com.base.dubbo.service.impl;

import com.alibaba.dubbo.rpc.RpcContext;
import com.base.dubbo.service.CallbackListener;
import com.base.dubbo.service.DemoService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DemoServiceImpl implements DemoService {

    private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<>();

    public DemoServiceImpl() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
                            entry.getValue().changed(getChange(entry.getKey()));
                        } catch (Exception e) {
                            listeners.remove(entry.getKey());
                        }
                    }
                    Thread.sleep(5000); // 定时触发变更通知
                } catch (Throwable e) {// 防御容错
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public String sayHello(String name) {
        System.out.println(name);
        return "Hello " + name;
    }

    @Override
    public void hello() {
        String fullString = RpcContext.getContext().getUrl().toFullString();
        System.out.println(fullString);
    }

    @Override
    public void addListener(String key, CallbackListener callbackListener) {
        listeners.put(key, callbackListener);
        callbackListener.changed(getChange(key));
    }

    private String getChange(String key) {
        System.out.println(key);
        return key + "---Changed: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}