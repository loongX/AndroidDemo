package com.example.basedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.basedemo.Json.GsonUtil;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserSimple userObject = new UserSimple(
                "Norman",
                "norman@futurestud.io",
                26,
                true
        );

        String gsonString = GsonUtil.GsonString(userObject);
        Log.i("TAG", gsonString);
        String userJson = "{'age':26,'email':'norman@futurestud.io','isDeveloper':true,'name':'Norman'}";
        UserSimple userObjec1 = GsonUtil.GsonToBean(userJson, UserSimple.class);
        Log.i("TAG", "serObjec1.name：" + userObjec1.name
                + "\n serObjec1.email：" + userObjec1.email
                + "\n serObjec1.age：" + userObjec1.age
                + "\n serObjec1.isDeveloper：" + userObjec1.isDeveloper);
    }

    public class UserSimple {
        String name;
        String email;
        int age;
        boolean isDeveloper;
        public UserSimple(String name, String email, int age, boolean isDeveloper){
            this.name = name;
            this.email = email;
            this.age = age;
            this.isDeveloper = isDeveloper;
        }
    }
}
