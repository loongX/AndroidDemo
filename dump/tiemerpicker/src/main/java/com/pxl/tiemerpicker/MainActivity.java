package com.pxl.tiemerpicker;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pxl.tiemerpicker.timepicker.PickerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlertDialog dialog;
    String hour="12";
    String minute="00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView= (TextView) findViewById(R.id.tv_timepicker);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDate();
                dialogData();
            }
        });

    }

    private void clearDate(){
        hour="12";
        minute="00";
    }

    private void dialogData() {
        dialog = new AlertDialog.Builder(this,R.style.Dialog).show();
        dialog.getWindow().setContentView(R.layout.time);

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.time, null);

        dialog.getWindow().setContentView(view);
        PickerView houe_pv = (PickerView) view.findViewById(R.id.minute_pv);
        PickerView houe_pv1 = (PickerView)view. findViewById(R.id.seton_pv);
        TextView tvCancel= (TextView) view.findViewById(R.id.quxiao);
        TextView tvSure= (TextView)view. findViewById(R.id.queren);



        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
                Toast.makeText(MainActivity.this, "当前时间 " + hour + " :" + minute, Toast.LENGTH_LONG).show();

            }
        });


        List<String> data = new ArrayList<>();
        List<String> data1 = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            data.add("" + i);
        }
        for (int i = 0; i < 60; i++) {
            data1.add(i < 10 ? "0" + i : "" + i);
        }
        houe_pv.setData(data);
        houe_pv.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                Toast.makeText(MainActivity.this, "选择了 " + text + " 小时", Toast.LENGTH_SHORT).show();
                hour=text;
            }
        });
        houe_pv1.setData(data1);
        houe_pv1.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                Toast.makeText(MainActivity.this, "选择了 " + text + " 分", Toast.LENGTH_SHORT).show();
                minute=text;
            }
        });
        houe_pv1.setSelected(0);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
