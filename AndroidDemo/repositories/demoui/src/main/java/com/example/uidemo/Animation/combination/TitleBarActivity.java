package com.example.uidemo.Animation.combination;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.uidemo.R;

/**
 * Created by pxl on 2017/9/29.
 */

public class TitleBarActivity extends Activity{
    private TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_titlebar);
        mTitleBar = (TitleBar) findViewById(R.id.title);

        mTitleBar.setLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TitleBarActivity.this, "左键被按", Toast.LENGTH_LONG).show();
            }
        });

        mTitleBar.setRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TitleBarActivity.this, "右键被按", Toast.LENGTH_LONG).show();

            }
        });
    }
}
