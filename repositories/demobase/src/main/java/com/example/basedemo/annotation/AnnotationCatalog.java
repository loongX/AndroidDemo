package com.example.basedemo.annotation;

import com.example.basedemo.annotation.diy.DIYAnnotationActivity;



/**
 * Created by loongago on 2017-06-21.
 */

public class AnnotationCatalog {

    public   Class[] CLAZZES = new Class[]{
            AnnotationActivity.class,//1
            DIYAnnotationActivity.class,//2
            ButterknifeActivity.class,//3
    };

    public String[] DESCRIBE = new String[] {
            "Annotation",//1
            "DIYAnnotation",//2
            "Butterknife",//3
    };
}
