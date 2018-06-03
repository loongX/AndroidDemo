package leadfair.observer;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Random;

import leadfair.observer.impl.AndroidPPObserver;
import leadfair.observer.test.Observable;
import leadfair.observer.test.Status;

public class MainActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    private AndroidPPObserver<Status> ppObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Observable observable = new Observable(getApplicationContext());
                Random random = new Random();
                while (true) {
                    Status afsgagds = new Status(random.nextInt(), "afsgagds");
                    Log.e(TAG, "--Observable-- no1 data is " + afsgagds);
                    observable.push(afsgagds);
                    SystemClock.sleep(20000);
                }
            }
        }).start();

        ppObserver = new AndroidPPObserver<Status>(getApplicationContext(), "sfasdgsfd") {
            @Override
            public void push(Status data) {
                Log.e(TAG, "--AndroidPPObserver-- no2 data is " + data.toString());
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);//停止1s
                while (true) {
                    long l = SystemClock.currentThreadTimeMillis();//获取系统时间
                    Status pull = ppObserver.pull();
                    Log.e(TAG, "time-" + (SystemClock.currentThreadTimeMillis() - l) + "");
                    Log.e(TAG, "time-:" + SystemClock.currentThreadTimeMillis());
                    if (pull != null) {
                        Log.e(TAG, "--AndroidPPObserver-- no3 data is " + pull.toString());
                    }
                    SystemClock.sleep(5000);
                }
            }
        }).start();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
