package com.loonglongago.annotation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loonglongago.annotation.view.ViewUtils;
import com.loonglongago.annotation.view.annotation.ContentView;
import com.loonglongago.annotation.view.annotation.ViewInject;
import com.loonglongago.annotation.view.annotation.event.OnClick;

//import com.pxl.base.view.ViewUtils;
//import com.pxl.base.view.annotation.ContentView;
//import com.pxl.base.view.annotation.ViewInject;
//import com.pxl.base.view.annotation.event.OnClick;


/**
 *
 * {@linkplain DoubleKeyValueMap heello}
 * {@link ViewInject}
 * @see android.view.LayoutInflater.Factory#
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        ViewUtils.injectContentView(this);

        ViewUtils.inject(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                textView.setText("OtherThread");
            }
        }).start();
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
