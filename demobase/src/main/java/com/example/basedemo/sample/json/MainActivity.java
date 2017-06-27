package com.example.basedemo.sample.json;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.basedemo.Json.GsonUtil;
import com.example.basedemo.R;
import com.example.basedemo.sample.json.bean.UserAddress;
import com.example.basedemo.sample.json.bean.UserSimpleOne;
import com.example.basedemo.sample.json.bean.UserSimpleTwo;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testUserSampleTwo();
        testUserSampleOne();
    }

    private void testUserSampleTwo() {
        String gsonString = GsonUtil.GsonString(Constant.userObject);
        Log.i("TAG", gsonString);
        String userJson = "{'age':26,'email':'norman@futurestud.io','isDeveloper':true,'name':'Norman'}";
        UserSimpleTwo userObjec1 = GsonUtil.GsonToBean(userJson, UserSimpleTwo.class);
        Log.i("TAG", "serObjec1.name：" + userObjec1.getName()
                + "\n serObjec1.email：" + userObjec1.getEmail()
                + "\n serObjec1.age：" + userObjec1.getAge()
                + "\n serObjec1.isDeveloper：" + userObjec1.isDeveloper());
    }

    private void testUserSampleOne() {


        String gsonString = GsonUtil.GsonString(Constant.userObjectOne);
        Log.i("TAG", gsonString);
        String userJson = "{'age':26,'email':'norman@futurestud.io','isDeveloper':true,'name':'Norman'}";
        UserSimpleOne userObjec1 = GsonUtil.GsonToBean(gsonString, UserSimpleOne.class);
        Log.i("TAG", "serObjec1.name：" + userObjec1.getName()
                + "\n serObjec1.email：" + userObjec1.getEmail()
                + "\n serObjec1.age：" + userObjec1.getAge()
                + "\n serObjec1.isDeveloper：" + userObjec1.getIsDeveloper());
    }


}
