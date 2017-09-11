package com.example.basedemo.annotation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.basedemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pxl on 2017/9/11.
 */

public class ButterknifeActivity extends AppCompatActivity {
    @BindView(R.id.textView)
    public TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        ButterKnife.bind(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("BindView 有效");
            }
        });
    }


    @OnClick(R.id.tb_2)
    public void onClick(View v) {
        textView.setText("在ButterknifeActivity里，你按了button2");
    }

    int count = 0;
    @OnClick(R.id.tb_1)
    public void onClick1(View v) {
        textView.setText("在ButterknifeActivity里，你按了button1,次数：" + (++count));
    }
}
