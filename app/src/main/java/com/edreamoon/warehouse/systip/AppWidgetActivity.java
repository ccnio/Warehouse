package com.edreamoon.warehouse.systip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.edreamoon.warehouse.R;

public class AppWidgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_widget);
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ExampleAppWidgetProvider.updateWidget();
            }
        });

    }
}
