package com.example.judge3g4g;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean is2G = NetworkEstimate.is2G(this);
        boolean is3g = NetworkEstimate.is3G(this);

        TextView tv = (TextView) findViewById(R.id.tv_network);
        tv.setText("is2G : " + is2G + "\nis3G" + is3g);
    }
}
