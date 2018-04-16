package com.edreamoon.warehouse.systip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.edreamoon.warehouse.R;

public class ThirdActivity extends AppCompatActivity {
    public static final int RESULT_Third = 0x33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_Third);
                finish();
            }
        });
    }

    public static void start(SecActivity secActivity, int codeThird) {
        Intent intent = new Intent(secActivity, ThirdActivity.class);
        secActivity.startActivityForResult(intent, codeThird);
    }
}
