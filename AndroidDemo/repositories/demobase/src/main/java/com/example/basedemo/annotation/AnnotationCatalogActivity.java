package com.example.basedemo.annotation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.basedemo.CategoryAdapter;
import com.example.basedemo.CategoryBean;
import com.example.basedemo.CategoryConstant1;
import com.example.basedemo.R;

public class AnnotationCatalogActivity extends AppCompatActivity {
    Context mContext;

    AnnotationCatalog category;
    CategoryBean categoryBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = AnnotationCatalogActivity.this;

        initData();
        showList(categoryBean);

    }

    private void initData(){
        category = new AnnotationCatalog();
        categoryBean = new CategoryBean(category.CLAZZES, category.DESCRIBE);

    }


    CategoryAdapter adapter;
    ListView listview;
    private void showList(CategoryBean bean) {
        if (adapter == null) {
            listview = (ListView) findViewById(R.id.listview_category);
//            listview.setInterface(this);
            adapter = new CategoryAdapter(mContext, bean);
            listview.setAdapter(adapter);
        } else {
            adapter.onDateChange(bean);
        }
    }

    private void launchOther(Class other){
        Intent intent  = new Intent(mContext,other);
        mContext.startActivity(intent);
    }
}
