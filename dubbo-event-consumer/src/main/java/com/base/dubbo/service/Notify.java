package com.base.dubbo.service;

import com.base.dubbo.domain.Person;

public interface Notify {
    public void onreturn(Person person,String id);
    public void onthrow(Throwable ex,String id);
    void oninvoke(String id);
}
