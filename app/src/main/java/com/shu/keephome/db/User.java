package com.shu.keephome.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 14623 on 2018/4/28.
 * 用户信息表
 */

public class User extends DataSupport{
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String name;
    String password;
    int age;
    int sex;
    String email;
    Boolean login;

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {

        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {

        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
