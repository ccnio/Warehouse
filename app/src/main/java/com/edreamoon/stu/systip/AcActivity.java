package com.edreamoon.stu.systip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.edreamoon.Utils;
import com.edreamoon.stu.R;

public class AcActivity extends AppCompatActivity {

    private static final String TAG = "AcActivity";
    public static final int REQ_SEC = 0x2;

    private Context mAppCtx = AcActivity.this;

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


        LinearLayout view = findViewById(R.id.container);
        view.removeView(null);
        Log.e("lijf", "onCreate: " + mAppCtx.getResources().getConfiguration().orientation);

        /**
         * 在启动第三方APK里的Activity之前，确定调用是否可以解析为一个Activity是一种很好的做法。
         *通过Intent的resolveActivity方法，并想该方法传入包管理器可以对包管理器进行查询以确定是否有Activity能够启动该Intent：
         */
        findViewById(R.id.bt2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the impliciy Intent to use to start a new Activity.
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:555-2368"));
// Check if an Activity exists to perform this action.
                PackageManager pm = mAppCtx.getPackageManager();
                ComponentName cn = intent.resolveActivity(pm);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (cn == null) {
                    // If there is no Activity available to perform the action
                    // Check to see if the Google Play Store is available.
                    Uri marketUri = Uri.parse("market://search?q=pname:com.myapp.packagename");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
                    // If the Google Play Store is available, use it to download an application
                    // capable of performing the required action. Otherwise log an error.
                    if (marketIntent.resolveActivity(pm) != null) {
                        AcActivity.this.startActivity(marketIntent);
                    } else {
                        Log.d(TAG, "Market client not available.");
                    }
                } else {
                    AcActivity.this.startActivity(intent);
                }
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
