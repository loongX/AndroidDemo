package com.example.basedemo.annotation.diy;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.basedemo.R;


@ContentView(R.layout.activity_annotation)
public class DIYAnnotationActivity extends AppCompatActivity {


    @ViewInject(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_annotation);
        ViewUtils.injectContentView(this);

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
