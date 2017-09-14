package com.slzr.dagger2t;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;


public class MainActivity extends AppCompatActivity {

    @Inject //标明需要注入的对象
    Person persion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 构造桥梁对象
        MainComponent component = MainComponent.builder().mainModule(new MainModule()).build();
        //注入
        component.inject(this);
    }
}
