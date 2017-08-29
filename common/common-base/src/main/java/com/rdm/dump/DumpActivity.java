package com.rdm.dump;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Rao on 2015/1/10.
 */
public class DumpActivity extends Activity {

    public void onCreate(Bundle b){
        super.onCreate(b);
        TextView tv = new TextView(this);
        tv.setText("DumpActivity");
        setContentView(tv);
    }
}
