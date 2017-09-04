package com.example.basedemo;

import com.example.basedemo.annotation.AnnotationActivity;
import com.example.basedemo.arouter.ArouterActivity;
import com.example.basedemo.ilog.IlogActivity;
import com.example.basedemo.key.KeyActivity;
import com.example.basedemo.listview.ListViewActivity;
import com.example.basedemo.sample.json.JsonParseActivity;
import com.example.basedemo.sample.json.multitypejson.MultiTypeJsonActivity;
import com.example.basedemo.threadpool.ThreadPoolActivity;
import com.example.basedemo.view.ui.InjectViewActivity;


/**
 * Created by loongago on 2017-06-21.
 */

public class CategoryConstant1 {

    public   Class[] CLAZZES = new Class[]{
            ListViewActivity.class,//1
            JsonParseActivity.class,//2
            MultiTypeJsonActivity.class,//3
            ArouterActivity.class,//4
            InjectViewActivity.class,//5
            ThreadPoolActivity.class,//6
            KeyActivity.class,//7
            IlogActivity.class,//8
            AnnotationActivity.class,//8

    };

    public String[] DESCRIBE = new String[] {
            "listview网络加载优化",//1
            "简单json解析",//2
            "复杂json解析",//3
            "Arouter跳转",//4
            "依赖注入",//5
            "线程池",//6
            "按键控制",//7
            "日记",//8
            "注解",//8
    };
}
