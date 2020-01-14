package com.ware.systip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ware.R;

public class SecActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQ_THIRD = 0x3;
    private static final String TAG = "SecActivity";
    public static final int RESULT_SEC = 0x22;
    private String MSG_OBJ = "abc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        Log.e("lijf", "onCreate: " + getIntent().getStringExtra("type"));
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.remove).setOnClickListener(this);


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
        switch (requestCode) {
            case REQ_THIRD:
                if (resultCode == ThirdActivity.RESULT_Third) {
                    Log.i(TAG, "onActivityResult: 2222");
                    setResult(RESULT_SEC);
                    finish();
                }
                break;
        }
    }

    public static void start(Context acActivity) {
        Intent intent = new Intent(acActivity, SecActivity.class);
        acActivity.startActivity(intent);
    }


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            Log.d(TAG, "handleMessage: " + (data == null));

        }
    };
    private static final int MSG_TIMEOUT = 0x11;
    private static final int TIMEOUT_MS = 1000 * 4;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String obj = "8b00c469com.wear.fantasy.xiaomi.glare.bird";
        if (id == R.id.add) {
//            if (!removeTask) {
//                LogUtil.d(TAG, "sendMsg: pkg = " + packageName + "; code = " + code + "; obj = " + obj);
            mHandler.removeMessages(MSG_TIMEOUT, obj);
            Message msg = mHandler.obtainMessage(MSG_TIMEOUT);
            Bundle data = new Bundle();
            data.putString("abc", "text");
            msg.setData(data);
            msg.obj = obj;
            mHandler.sendMessageDelayed(msg, TIMEOUT_MS);
        } else if (id == R.id.remove) {
            mHandler.removeMessages(MSG_TIMEOUT, obj);
            mHandler.removeMessages(11, MSG_OBJ);
            String str1 = "ab",str2 = "cd";
            String str3 = "ab",str4 = "cd";
            String a = str1 + str2;
            String b = "abc" + "a";
            Log.d(TAG, "onClick: equal " + (a == b));
        }
    }
}
