package com.example.subcriber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.subcriber.base.App;
import com.example.subcriber.base.Subscriber;
import com.example.subcriber.event.TestEvent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestEvent event = new TestEvent();
                event.mes = "测试成功！次数：";
                App.get().getPublisher().publish(event);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregister();
    }

    private void register() {
        App.get().getPublisher().subscribe(TestEvent.class, testEventSubscriber);
    }

    private void unregister() {
        App.get().getPublisher().unsubscribe(testEventSubscriber);
    }

    int count = 0;
    Subscriber<TestEvent> testEventSubscriber = new Subscriber<TestEvent>() {
        @Override
        public void onEvent(TestEvent event) {
            count++;
            String s = event.mes + count;
            ((TextView)findViewById(R.id.tv_content)).setText(s);
        }
    };


}
