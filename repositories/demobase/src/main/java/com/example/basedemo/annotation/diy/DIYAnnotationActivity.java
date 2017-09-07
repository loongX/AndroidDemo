package com.example.basedemo.annotation.diy;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.basedemo.R;
import com.example.basedemo.annotation.view.annotation.event.*;


public class DIYAnnotationActivity extends AppCompatActivity {


    @ViewInject(value = R.id.textView,parentId = 0)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        ViewUtils.inject(this);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("成功了！");
            }
        });

    }

    @OnClick(R.id.tb_2)
    public void onClick(View v) {
        textView.setText("你按了button2");
    }

}
