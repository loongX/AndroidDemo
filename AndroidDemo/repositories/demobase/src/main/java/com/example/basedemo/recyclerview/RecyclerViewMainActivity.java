package com.example.basedemo.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.basedemo.R;


/**
 * Created by pxl on 2017/9/8.
 */

public class RecyclerViewMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_main);
    }

    public void showListView(View view){
        startActivity(new Intent(this,ListViewActivity.class));
    }

    public void showGridView(View view) {
        startActivity(new Intent(this,GridViewActivity.class));
    }
}
