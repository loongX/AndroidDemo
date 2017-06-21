package com.example.androiddemo.code.jnitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.androiddemo.R;

public class JniActivity extends AppCompatActivity {

    //动态加载库
    static {
        System.loadLibrary("TestJNI");
    }

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jnitest);
        tv = (TextView) findViewById(R.id.text_hello);
        Jni jni = new Jni();
        tv.setText(jni.say("Hello world. This message is from Jni."));

    }
}
