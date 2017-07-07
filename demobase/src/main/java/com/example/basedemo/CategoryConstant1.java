package com.example.basedemo;

import com.example.basedemo.arouter.ArouterActivity;
import com.example.basedemo.listview.ListViewActivity;
import com.example.basedemo.sample.json.JsonParseActivity;
import com.example.basedemo.sample.json.multitypejson.MultiTypeJsonActivity;


/**
 * Created by loongago on 2017-06-21.
 */

public class CategoryConstant1 {

    public   Class[] CLAZZES = new Class[]{
            ListViewActivity.class,//1
            JsonParseActivity.class,//2
            MultiTypeJsonActivity.class,//3
            ArouterActivity.class,//4

    };

    public String[] DESCRIBE = new String[] {
            "listview网络加载优化",//1
            "简单json解析",//2
            "复杂json解析",//3
            "Arouter跳转",//4
    };
}
