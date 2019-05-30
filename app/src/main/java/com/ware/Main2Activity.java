package com.ware;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianfeng.li on 2017/11/24.
 */

public class Main2Activity extends AppCompatActivity {
    public static String TAG = Main2Activity.class.getName();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPackageList();
            }
        });
    }

    private void getPackageList() {
        long start = System.currentTimeMillis();
        ArrayList<AppInfo> appList = new ArrayList<>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {

            PackageInfo info = packages.get(i);

            AppInfo tmpInfo = new AppInfo();

            tmpInfo.appName = info.applicationInfo.loadLabel(getPackageManager()).toString();

            tmpInfo.packageName = info.packageName;

            tmpInfo.versionName = info.versionName;

            tmpInfo.versionCode = info.versionCode;

            tmpInfo.appIcon = info.applicationInfo.loadIcon(getPackageManager());

            appList.add(tmpInfo);


            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                Log.d(TAG, "*************************");
                Log.d(TAG, "Name:" + tmpInfo.appName + " drawable:" + tmpInfo.appIcon);
                Log.d(TAG, "userid:" + info.sharedUserId + " flags: ");
            }
//            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){// 非系统应用
//                tmpInfo.print();
//            } else {
//
//
        }
        Log.d(TAG, "getPackageList: " + (System.currentTimeMillis() - start));

    }


    public class AppInfo {
        public String appName = "";
        public String packageName = "";
        public String versionName = "";
        public int versionCode = 0;
        public Drawable appIcon = null;

        public void print() {
            Log.v(TAG, "Name:" + appName + " Package:" + packageName + " drawable: " + appIcon);
        }
    }
}