package com.edreamoon.stu;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by jianfeng.li on 2017/11/24.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String DEFAULT_COVERAGE_FILE_PATH = Environment.getExternalStorageDirectory().getPath();
    public static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.bt1).setOnClickListener(this);


        String path = "/mtrace";
        Debug.startMethodTracing(Environment.getExternalStorageDirectory().getPath() + path);
        Log.e("lijf", "onCreate: " + path);

        rub();
    }

    private void rub() {
        SystemClock.sleep(3000);
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Debug.stopMethodTracing();
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bt1() {
        for (int i = 0; i < 10000; i++) {
            System.out.print(i);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt1) {
            bt1();
        }
    }
}