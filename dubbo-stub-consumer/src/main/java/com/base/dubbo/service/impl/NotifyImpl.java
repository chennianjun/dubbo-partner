package com.base.dubbo.service.impl;

import com.base.dubbo.domain.Person;
import com.base.dubbo.service.Notify;

import java.util.HashMap;
import java.util.Map;

public class NotifyImpl implements Notify {

    public Map<String, Person> ret = new HashMap<>();
    public Map<String, Throwable> errors = new HashMap<>();

    @Override
    public void onreturn(Person person, String id) {
        System.out.println("onreturn:" + person);
        ret.put(id, person);
    }

    @Override
    public void onthrow(Throwable ex, String id) {
        System.out.println("ex = [" + ex + "], id = [" + id + "]");
        errors.put(id, ex);
    }

    @Override
    public void oninvoke(String id) {
        System.out.println("id = [" + id + "]");
    }
}
