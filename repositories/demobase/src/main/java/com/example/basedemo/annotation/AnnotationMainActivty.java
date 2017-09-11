package com.example.basedemo.annotation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.basedemo.R;
import com.example.basedemo.annotation.diy.DIYAnnotationActivity;


/**
 * Created by pxl on 2017/9/11.
 */

public class AnnotationMainActivty extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_main);
    }

    public void jumpButterknifeActivity(View view){
        startActivity(new Intent(this,ButterknifeActivity.class));
    }

    public void jumpAnnotationActivity(View view){
        startActivity(new Intent(this,AnnotationActivity.class));
    }

    public void jumpDIYAnnotationActivity(View view) {
        startActivity(new Intent(this,DIYAnnotationActivity.class));
    }

}
