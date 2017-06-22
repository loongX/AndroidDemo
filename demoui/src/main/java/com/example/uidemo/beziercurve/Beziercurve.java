package com.example.uidemo.beziercurve;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.uidemo.R;
import com.example.uidemo.beziercurve.view.WaveView;

/**
 * Created by loongago on 2017-06-22.
 */

public class Beziercurve extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beziercurve);
        WaveView waveView = (WaveView) findViewById(R.id.waveview);
//        waveView.performClick();
    }
}
