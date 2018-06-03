package com.example.basedemo.sample.json.multitypejson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.basedemo.Json.ListItemFilter;
import com.example.basedemo.Json.MultiTypeJsonParser;
import com.example.basedemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.basedemo.sample.json.multitypejson.bean.AddressAttribute;
import com.example.basedemo.sample.json.multitypejson.bean.Attribute;
import com.example.basedemo.sample.json.multitypejson.bean.AttributeWithType;
import com.example.basedemo.sample.json.multitypejson.bean.ListInfoNoType;
import com.example.basedemo.sample.json.multitypejson.bean.ListInfoWithType;
import com.example.basedemo.sample.json.multitypejson.bean.NameAttribute;
import com.example.basedemo.sample.json.multitypejson.bean.generic.BaseListInfo;
import com.example.basedemo.sample.json.multitypejson.bean.generic.BaseUpperBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MultiTypeJsonActivity extends AppCompatActivity {

    private static final String TAG = "MultiTypeJsonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multitypejson);
    }

    public void generalParse(View view) throws JSONException {
        ListInfoWithType listInfoWithType = new ListInfoWithType();
        JSONObject jsonObject = new JSONObject(TestJson.TEST_JSON_1);
        int total = jsonObject.getInt("total");
        JSONArray jsonArray = jsonObject.getJSONArray("list");
        Gson gson = new Gson();
        List<AttributeWithType> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject innerJsonObject = jsonArray.getJSONObject(i);
            Class<? extends Attribute> clazz;
            String type = innerJsonObject.getString("type");
            if (TextUtils.equals(type, "address")) {
                clazz = AddressAttribute.class;
            } else if (TextUtils.equals(type, "name")) {
                clazz = NameAttribute.class;
            } else {
                //有未知的类型就跳过
                continue;
            }
            AttributeWithType attributeWithType = new AttributeWithType();
            Attribute attribute = gson.fromJson(innerJsonObject.getString("attributes"), clazz);
            attributeWithType.setType(type);
            attributeWithType.setAttributes(attribute);
            list.add(attributeWithType);
        }

        listInfoWithType.setTotal(total);
        listInfoWithType.setList(list);

        Log.d(TAG, "generalParse: " + listInfoWithType);
    }

    public void parseAttribute1(View view) {
        MultiTypeJsonParser<Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<Attribute>()
                .registerTypeElementName("type")
                .registerTargetClass(Attribute.class)
                .registerTargetUpperLevelClass(AttributeWithType.class)
                .registerTypeElementValueWithClassType("address", AddressAttribute.class)
                .registerTypeElementValueWithClassType("name", NameAttribute.class)
                .build();

        ListInfoWithType listInfoWithType = multiTypeJsonParser.fromJson(TestJson.TEST_JSON_1, ListInfoWithType.class);
        Log.i(TAG, "parseAttribute1: " + listInfoWithType.getTotal() + "   " + listInfoWithType.getList().get(0) + "   " + listInfoWithType.getList().get(1) );
    }

    public void parseAttribute2(View view) {
        MultiTypeJsonParser<Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<Attribute>()
                .registerTypeElementName("type")
                .registerTargetClass(Attribute.class)
                .registerTypeElementValueWithClassType("address", AddressAttribute.class)
                .registerTypeElementValueWithClassType("name", NameAttribute.class)
                .build();

        ListInfoNoType listInfoNoType = multiTypeJsonParser.fromJson(TestJson.TEST_JSON_2, ListInfoNoType.class);
        Log.d(TAG, "parseAttribute2: " + listInfoNoType);
    }

    /**
     * 包含未知类型
     *
     * @param view
     */
    public void parseWithUnknownType1(View view) {
        MultiTypeJsonParser<Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<Attribute>()
                .registerTypeElementName("type")
                .registerTargetClass(Attribute.class)
                .registerTargetUpperLevelClass(AttributeWithType.class)
                .registerTypeElementValueWithClassType("address", AddressAttribute.class)
                .registerTypeElementValueWithClassType("name", NameAttribute.class)
                .build();

        ListInfoWithType listInfoWithType = multiTypeJsonParser.fromJson(TestJson.TEST_JSON_WITH_UNKNOWN_TYPE1, ListInfoWithType.class);

        //如果含有未知的type类型，解析的集合中包含null，过滤一下
        listInfoWithType.setList(ListItemFilter.filterNullElement(listInfoWithType.getList()));
        Log.d(TAG, "parseWithUnknownType listSize: " + listInfoWithType.getList().size());
    }

    /**
     * 包含未知类型
     *
     * @param view
     */
    public void parseWithUnknownType2(View view) {
        MultiTypeJsonParser<Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<Attribute>()
                .registerTypeElementName("type")
                .registerTargetClass(Attribute.class)
                .registerTypeElementValueWithClassType("address", AddressAttribute.class)
                .registerTypeElementValueWithClassType("name", NameAttribute.class)
                .build();

        ListInfoNoType listInfoNoType = multiTypeJsonParser.fromJson(TestJson.TEST_JSON_WITH_UNKNOWN_TYPE2, ListInfoNoType.class);
        listInfoNoType.setList(ListItemFilter.filterNullElement(listInfoNoType.getList()));
        Log.d(TAG, "parseWithUnknownType2: " + listInfoNoType);
    }

    public void parseWithGeneric(View view) {
        Type upperClass = new TypeToken<BaseUpperBean<Attribute>>() {
        }.getType();
        MultiTypeJsonParser<Attribute> multiTypeJsonParser = new MultiTypeJsonParser.Builder<Attribute>()
                .registerTypeElementName("type")
                .registerTargetClass(Attribute.class)
                .registerTargetUpperLevelClass(upperClass)
                .registerTypeElementValueWithClassType("address", AddressAttribute.class)
                .registerTypeElementValueWithClassType("name", NameAttribute.class)
                .build();

        Type listInfoType = new TypeToken<BaseListInfo<BaseUpperBean<Attribute>>>() {
        }.getType();
        BaseListInfo<BaseUpperBean<Attribute>> listInfo = multiTypeJsonParser.fromJson(TestJson.TEST_JSON_1, listInfoType);
        Log.d(TAG, "parseWithGeneric: " + listInfo);
    }
}
