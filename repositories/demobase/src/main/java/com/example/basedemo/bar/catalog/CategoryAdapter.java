package com.example.basedemo.bar.catalog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.basedemo.R;
import com.example.basedemo.bar.Bar;
import com.example.basedemo.bar.StatusBarActivity;

public class CategoryAdapter extends BaseAdapter {
    CategoryBean bean;
    LayoutInflater inflater;
    Context mContext;


    public CategoryAdapter(Context context, CategoryBean bean) {
        this.bean = bean;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;

    }

    public void onDateChange(CategoryBean bean) {
        this.bean = bean;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        return bean.getDESCRIBE().length;
    }

    @Override
    public Object getItem(int position) {
        String[] describe = bean.getDESCRIBE();
        return describe[position];
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String[] describe = bean.getDESCRIBE();
        final Class[] CLAZZ = bean.getCLAZZES();

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_category, null);
            holder.tv_describe = (TextView) convertView
                    .findViewById(R.id.item_class_describe);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_describe.setText(describe[position]);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {//第0个位沉浸栏，1开始才是bar里面定义的
                    Bar.setStatus(Bar.values()[position - 1]);
                }
                launchOther(CLAZZ[position]);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_describe;
    }

    private void launchOther(Class other) {
        Intent intent = new Intent(mContext, other);
        mContext.startActivity(intent);
    }
}
