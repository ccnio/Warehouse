package com.edreamoon.stu.systip;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import com.edreamoon.stu.R;

public class SecActivity extends AppCompatActivity {
    public static final int REQ_THIRD = 0x3;
    private static final String TAG = "SecActivity";
    public static final int RESULT_SEC = 0x22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        Log.e("lijf", "onCreate: " + getIntent().getStringExtra("type"));

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThirdActivity.start(SecActivity.this, REQ_THIRD);
                /**
                 * 添加finish后 onActivityResult 均再收不到回传
                 */
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_THIRD:
                if(resultCode == ThirdActivity.RESULT_Third) {
                    Log.i(TAG, "onActivityResult: 2222" );
                    setResult(RESULT_SEC);
                    finish();
                }
                break;
        }
    }

    public static void start(AcActivity acActivity, int code) {
        Intent intent = new Intent(acActivity, SecActivity.class);
        Bundle bundle = new Bundle(3);
        bundle.putString("type","common type");
        intent.putExtras(bundle);
        acActivity.startActivityForResult(intent, code);
    }
}
