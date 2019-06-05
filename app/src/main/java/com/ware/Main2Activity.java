package com.ware;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
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
                getAllApps(getApplicationContext());
                Log.d(TAG, "*************************************************");
                getLauncherApp();
            }
        });
    }

    private void getLauncherApp() {
        long millis = System.currentTimeMillis();
        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setAction(Intent.ACTION_MAIN);

        PackageManager manager = getPackageManager();
        List<ResolveInfo> resolveInfoList = manager.queryIntentActivities(intent, 0);
//        manager.query
        for (int i = 0; i < resolveInfoList.size(); i++) {
            ResolveInfo info = resolveInfoList.get(i);
            Log.d(TAG, "getLauncherApp: " + info.loadLabel(getPackageManager()) + " " + info.activityInfo.packageName + "  "
                    + info.activityInfo.applicationInfo.loadLabel(getPackageManager())
                    + "  " + info.activityInfo.applicationInfo.loadIcon(getPackageManager())
            );
            Log.d(TAG, "getLauncherApp: " + info.activityInfo.icon + "  " + info.activityInfo.applicationInfo.icon);
//            Log.d(TAG, "getLauncherApp: " + info.activityInfo.name + " " + info.labelRes);
        }
//        logd
    }

    public List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
                Log.d(TAG, "getAllApps: " + pak.applicationInfo.loadLabel(getPackageManager())
                        + "  " + pak.applicationInfo.loadIcon(getPackageManager()));
            }
        }
        return apps;
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