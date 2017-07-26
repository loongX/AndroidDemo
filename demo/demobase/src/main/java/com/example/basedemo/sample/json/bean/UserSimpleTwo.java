package com.example.basedemo.sample.json.bean;

/**
 * Created by Administrator on 2017/6/27.
 */

public class UserSimpleTwo {
    String name;
    String email;
    int age;
    boolean isDeveloper;

    public UserSimpleTwo(String name, String email, int age, boolean isDeveloper){
        this.name = name;
        this.email = email;
        this.age = age;
        this.isDeveloper = isDeveloper;
    }

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

    public boolean isDeveloper() {
        return isDeveloper;
    }

    public void setDeveloper(boolean developer) {
        isDeveloper = developer;
    }
}
