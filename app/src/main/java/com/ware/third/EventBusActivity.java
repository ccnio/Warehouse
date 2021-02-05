package com.ware.third;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ware.R;

import org.greenrobot.eventbus.EventBus;

public class EventBusActivity extends AppCompatActivity {
    private static final String TAG = "RxJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus);
        EventBus.getDefault().register(this);
        findViewById(R.id.sendView).setOnClickListener(v -> EventBus.getDefault().post(new Event()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
