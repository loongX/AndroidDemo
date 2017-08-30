package com.example.ilog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ilog.base.ILog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //由于log是很基本的组件，所以一开始就初始化
        ILog.setDelegate(new ILogImpl());
        ILog.i("main", "test i");
        ILog.d("main", "test d");
        ILog.e("main", "test e");
        ILog.v("main", "test v");
    }
}
