package com.example.basedemo.sample.json;

//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.basedemo.Json.GsonUtil;
import com.example.basedemo.R;
import com.example.basedemo.sample.json.Constant;
import com.example.basedemo.sample.json.bean.Attributes;
import com.example.basedemo.sample.json.bean.Ulist;
import com.example.basedemo.sample.json.bean.UserAddress;
import com.example.basedemo.sample.json.bean.UserSimpleOne;
import com.example.basedemo.sample.json.bean.UserSimpleThree;
import com.example.basedemo.sample.json.bean.UserSimpleTwo;

import java.util.ArrayList;
import java.util.List;


public class JsonParseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsonparse);

        testUserSampleTwo();
        testUserSampleOne();
        testUserSampleThree();
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

    private void testUserSampleThree() {
        Attributes attributes1 = new Attributes().withStreet("NanJing Road")
                .withCity("ShangHai")
                .withCity("China");

        Ulist ulist1 = new Ulist()
                .withAttributes(attributes1)
                .withType("address");

        Attributes attributes2 = new Attributes()
                .withFirstName("Su")
                .withLastName("Tu");
        Ulist ulist2 = new Ulist()
                .withAttributes(attributes2)
                .withType("address");
        List<Ulist> ulist = new ArrayList();
        ulist.add(ulist1);
        ulist.add(ulist2);
        UserSimpleThree userSimpleThree = new UserSimpleThree().withTotal(2).withUlist(ulist);
  /*      {
            "total": 2,
                "ulist": [
            {
                "type": "address",
                    "attributes": {
                "street": "NanJing Road",
                        "city": "ShangHai",
                        "country": "China"
            }
            },
            {
                "type": "name",
                    "attributes": {
                "first-name": "Su",
                        "last-name": "Tu"
            }
            }
    ]
        }*/

        String gsonString = GsonUtil.GsonString(userSimpleThree);
        Log.i("TAG3", gsonString);
        String userJson = "{\"total\":2,\"ulist\":[{\"attributes\":{\"city\":\"China\",\"street\":\"NanJing Road\"}," +
                "\"type\":\"address\"},{\"attributes\":{\"first-name\":\"Su\",\"last-name\":\"Tu\"},\"type\":\"address\"}]}";
        UserSimpleThree userObjec1 = GsonUtil.GsonToBean(gsonString, UserSimpleThree.class);
        Log.i("TAG", "serObjec1.name：" + userObjec1.getTotal()
                + "\n serObjec1.email：" + userObjec1.getUlist().get(1).getAttributes().getCity()
                + "\n serObjec1.email：" + userObjec1.getUlist().get(1).getAttributes().getCountry()
                + "\n serObjec1.email：" + userObjec1.getUlist().get(1).getAttributes().getFirstName()
               );
    }


}
