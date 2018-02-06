package com.edreamoon.stu.systip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.edreamoon.stu.R;

public class AcActivity extends AppCompatActivity{

    private static final String TAG = "AcActivity";
    public static final int REQ_SEC = 0x2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_ac);
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecActivity.start(AcActivity.this, REQ_SEC);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_SEC:
                if (resultCode == SecActivity.RESULT_SEC) {
                    Log.i(TAG, "onActivityResult: 111");
                }
                break;
        }
    }
}
