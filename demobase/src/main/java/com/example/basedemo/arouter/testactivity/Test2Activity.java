package com.example.basedemo.arouter.testactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basedemo.R;

/**
 * Created by Administrator on 2017/7/7.
 */
@Route(path = "/test/activity2")
public class Test2Activity extends AppCompatActivity {
    @Autowired
    Long key1;

    @Autowired
    String key3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ARouter.getInstance().inject(this);
        TextView tvContent = (TextView) findViewById(R.id.tv_testcontent);
        String rec1 = "key1 %s " + key1 +"key2 :" + key3;
        String rec2 = String.format("key1 %s key2 %s" , key1 , key3);
        tvContent.setText("test2" );
//        String value = getIntent().getStringExtra("key1");
//        if (!TextUtils.isEmpty(value)) {
            Toast.makeText(this, "exist param :" + rec2 , Toast.LENGTH_LONG).show();
//        }
    }
}
