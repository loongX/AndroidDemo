package com.example.basedemo.sample.json.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class UserSimpleOne {

    @SerializedName("age")
    @Expose
    private Integer age;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("isDeveloper")
    @Expose
    private Boolean isDeveloper;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("userAddress")
    @Expose
    private UserAddress userAddress;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public UserSimpleOne withAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserSimpleOne withEmail(String email) {
        this.email = email;
        return this;
    }

    public Boolean getIsDeveloper() {
        return isDeveloper;
    }

    public void setIsDeveloper(Boolean isDeveloper) {
        this.isDeveloper = isDeveloper;
    }

    public UserSimpleOne withIsDeveloper(Boolean isDeveloper) {
        this.isDeveloper = isDeveloper;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserSimpleOne withName(String name) {
        this.name = name;
        return this;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }

    public UserSimpleOne withUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
        return this;
    }



}
