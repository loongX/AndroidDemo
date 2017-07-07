package com.example.basedemo.arouter.testactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.basedemo.R;

/**
 * Created by Administrator on 2017/7/7.
 */
@Route(path = "/test/activity1")
public class Test1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        TextView tvContent = (TextView) findViewById(R.id.tv_testcontent);
        tvContent.setText("test1");
    }
}
