package com.base.dubbo.api;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @project:dubbo-application
 * @package:com.base.dubbo.api
 * @create_date:2018/1/23 10:40
 * @author:Subtimental
 * @description:TODO
 */
public class ValidationParameter implements Serializable{
    @NotNull // disallow null
    @Size(min = 2, max = 20) // min and max
    private String name;

    @NotNull(groups = ValidationService.Save.class) // disallow null when save, but allow null when update, that is: not update
    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
    private String email;

    @Min(18) // min
    @Max(100) // max
    private int age;

    @Past // must be a past time
    private Date loginDate;

    @Future // must be a future time
    private Date expiryDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
