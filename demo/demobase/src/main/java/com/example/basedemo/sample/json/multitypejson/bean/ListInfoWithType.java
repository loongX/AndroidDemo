package com.example.basedemo.sample.json.multitypejson.bean;

import java.util.List;

/**
 * Created by yunlong.su on 2017/6/15.
 */

public class ListInfoWithType {
    private int total;
    private List<AttributeWithType> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<AttributeWithType> getList() {
        return list;
    }

    public void setList(List<AttributeWithType> list) {
        this.list = list;
    }
}
