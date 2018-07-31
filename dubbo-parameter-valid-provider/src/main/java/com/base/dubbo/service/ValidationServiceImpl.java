package com.base.dubbo.service;

import com.base.dubbo.api.ValidationParameter;
import com.base.dubbo.api.ValidationService;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo.service
 * @create_date:2018/1/23 10:44
 * @author:Subtimental
 * @description:TODO
 */
public class ValidationServiceImpl implements ValidationService {
    @Override
    public void save(ValidationParameter parameter) {
        System.out.println("ValidationServiceImpl.save");
    }

    @Override
    public void update(ValidationParameter parameter) {
        System.out.println("ValidationServiceImpl.update");
    }

    @Override
    public void delete(long id, String operator) {
        System.out.println("ValidationServiceImpl.delete");
    }
}
