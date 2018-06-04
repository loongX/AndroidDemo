package com.googu.a30809.wave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author 30809
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 自动增长进度从0到100
      //  ((WaveView) findViewById(R.id.wave)).startIncrease();
    }
}
