package leadfair.observer;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Random;

import leadfair.observer.impl.AndroidPPObserver;
import leadfair.observer.test.Observable;
import leadfair.observer.test.Status;

public class LoginActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    private AndroidPPObserver<Status> ppObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    }
}
