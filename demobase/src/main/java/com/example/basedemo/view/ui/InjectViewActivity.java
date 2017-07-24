package com.example.basedemo.view.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.basedemo.R;
import com.example.basedemo.view.annotation.ContentView;
import com.example.basedemo.view.annotation.Event;
import com.example.basedemo.view.annotation.ViewInject;
import com.example.basedemo.view.x;

@ContentView(R.layout.activity_inject_view)
public class InjectViewActivity extends AppCompatActivity {

    @ViewInject(R.id.tv_content)
    TextView tv_content;
    @ViewInject(R.id.bt_hello)
    Button bt_hello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_inject_view);
        x.Ext.init(getApplication());
        x.view().inject(this);

        tv_content.setText("change");


    }

    int conunt = 0;
    @Event(R.id.bt_hello)
    private void onClick(){
        conunt++;
        tv_content.setText("do more " + conunt);
        Toast.makeText(this, "hello" , Toast.LENGTH_LONG).show();
    }

}
