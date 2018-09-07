package com.edreamoon.warehouse.systip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.edreamoon.warehouse.R;
import com.edreamoon.warehouse.dialog.PopupActivity;

public class ThirdActivity extends AppCompatActivity {
    public static final int RESULT_Third = 0x33;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(PopupActivity.TAG, "onDestroy ------------- Third: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(PopupActivity.TAG, "onStop ------------- Third: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(PopupActivity.TAG, "onPause  ------------- Third: ");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(PopupActivity.TAG, "onWindowFocusChanged ------------- Third: " + hasFocus);
    }

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
