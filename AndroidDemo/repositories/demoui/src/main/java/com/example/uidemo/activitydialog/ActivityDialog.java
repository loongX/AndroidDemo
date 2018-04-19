package com.example.uidemo.activitydialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uidemo.R;

public class ActivityDialog extends Activity {

    public static void launch(Context context){
        Intent intent  = new Intent(context,ActivityDialog.class);
        context.startActivity(intent);
    }

    private TextView bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_dialog);
        bt = (TextView) findViewById(R.id.bt);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityDialog.this,"后台下载中",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


}
