package com.example.uidemo.activitydialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.uidemo.R;

public class DialogControl extends AppCompatActivity {

    public static void launch(Context context){
        Intent intent  = new Intent(context,DialogControl.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogcontrol);
        ActivityDialog.launch(this);
    }

    public void showDialog(View view){
        Intent i = new Intent(this,ActivityDialog.class);
        startActivity(i);
    }
}
