package com.example.basedemo.arouter;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basedemo.R;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ArouterActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aroutermain);
        if (true) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(getApplication()); // 尽可能早，推荐在Application中初始化
        findViewById(R.id.bt_totest1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/test/activity1").navigation();
            }
        });
        findViewById(R.id.bt_totest2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/test/activity2").navigation();
                // 2. 跳转并携带参数
                ARouter.getInstance().build("/test/activity2")
                        .withString("key1", "666L")
//                        .withString("key3", "888")
//                        .withObject("key4", new Test("Jack", "Rose"))
                        .navigation();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
// 1. 应用内简单的跳转(通过URL跳转在'进阶用法'中)
//        ARouter.getInstance().build("/test/activity1").navigation();
    }

    @Override
    public void onClick(View v) {

    }
}
