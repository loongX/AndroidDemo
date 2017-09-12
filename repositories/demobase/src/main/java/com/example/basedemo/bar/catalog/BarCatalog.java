package com.example.basedemo.bar.catalog;

import com.example.basedemo.annotation.AnnotationCatalogActivity;
import com.example.basedemo.arouter.ArouterActivity;
import com.example.basedemo.bar.HideStatusBarActivity;
import com.example.basedemo.bar.StatusBarActivity;
import com.example.basedemo.dagger2.Dagger2Activity;
import com.example.basedemo.ilog.IlogActivity;
import com.example.basedemo.key.KeyActivity;
import com.example.basedemo.listview.ListViewActivity;
import com.example.basedemo.recyclerview.RecyclerViewMainActivity;
import com.example.basedemo.sample.json.JsonParseActivity;
import com.example.basedemo.sample.json.multitypejson.MultiTypeJsonActivity;
import com.example.basedemo.threadpool.ThreadPoolActivity;
import com.example.basedemo.view.ui.InjectViewActivity;
import com.example.basedemo.webview.WebViewActivity;


/**
 * Created by loongago on 2017-06-21.
 */

public class BarCatalog {

    public   Class[] CLAZZES = new Class[]{
            HideStatusBarActivity.class,//1
            StatusBarActivity.class,//2

    };

    public String[] DESCRIBE = new String[] {
            "隐藏状态栏",//1
            "沉浸栏",//2

    };
}
