package com.ware;

import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jianfeng.li on 2017/11/24.
 */

public class Main2Activity extends AppCompatActivity {
    public static String TAG = Main2Activity.class.getName();
    private RecyclerView mRecyclerView;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mView = findViewById(R.id.rotate_view);
        findViewById(R.id.timeView).setOnClickListener(v -> {
            Time nextInstanceTime = new Time();
            nextInstanceTime.setToNow();
            nextInstanceTime.second = 0;
            nextInstanceTime.hour = 12;
            nextInstanceTime.minute = 20;
            nextInstanceTime.monthDay += 2;
            Log.d("Main2Activity", "onCreate: " + nextInstanceTime.toString());
            Log.d("Main2Activity", "onCreate222: " + nextInstanceTime.normalize(true));

        });

//        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setRepeatMode(RotateAnimation.RESTART);
//        rotate.setRepeatCount(RotateAnimation.INFINITE);
//        rotate.setDuration(2000);
////        rotate.setInterpolator(new LinearInterpolator());
//
//        AlphaAnimation alpha = new AlphaAnimation(0.2f, 1.0f);
//        alpha.setDuration(2000);
//        alpha.setRepeatMode(RotateAnimation.RESTART);
//        rotate.setRepeatCount(RotateAnimation.INFINITE);
//
//        AnimationSet set = new AnimationSet(true);
//        set.addAnimation(rotate);
//        set.addAnimation(alpha);


//
//        set.setRepeatMode(AnimationSet.RESTART);
//        set.setRepeatCount(10);
//        set.setDuration(2000);
//
//
//        mView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mView.startAnimation(set);
//            }
//        }, 1000);

    }
//    private void getLauncherApp() {
//        long millis = System.currentTimeMillis();
//        // 桌面应用的启动在INTENT中需要包含ACTION_MAIN 和CATEGORY_HOME.
//        Intent intent = new Intent();
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.setAction(Intent.ACTION_MAIN);
//
//        PackageManager manager = getPackageManager();
//        List<ResolveInfo> resolveInfoList = manager.queryIntentActivities(intent, 0);
////        manager.query
//        for (int i = 0; i < resolveInfoList.size(); i++) {
//            ResolveInfo info = resolveInfoList.get(i);
//            Log.d(TAG, "getLauncherApp: " + info.loadLabel(getPackageManager()) + " " + info.activityInfo.packageName + "  "
//                    + info.activityInfo.applicationInfo.loadLabel(getPackageManager())
//                    + "  " + info.activityInfo.applicationInfo.loadIcon(getPackageManager())
//            );
//            Log.d(TAG, "getLauncherApp: " + info.activityInfo.icon + "  " + info.activityInfo.applicationInfo.icon);
////            Log.d(TAG, "getLauncherApp: " + info.activityInfo.name + " " + info.labelRes);
//        }
////        logd
//    }
//
//    public List<PackageInfo> getAllApps(Context context) {
//        List<PackageInfo> apps = new ArrayList<PackageInfo>();
//        PackageManager pManager = context.getPackageManager();
//        //获取手机内所有应用
//        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
//        for (int i = 0; i < paklist.size(); i++) {
//            PackageInfo pak = (PackageInfo) paklist.get(i);
//            //判断是否为非系统预装的应用程序
//            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
//                // customs applications
//                apps.add(pak);
//                Log.d(TAG, "getAllApps: " + pak.applicationInfo.loadLabel(getPackageManager())
//                        + "  " + pak.applicationInfo.loadIcon(getPackageManager()));
//            }
//        }
//        return apps;
//    }
//
//
//    public class AppInfo {
//        public String appName = "";
//        public String packageName = "";
//        public String versionName = "";
//        public int versionCode = 0;
//        public Drawable appIcon = null;
//
//        public void print() {
//            Log.v(TAG, "Name:" + appName + " Package:" + packageName + " drawable: " + appIcon);
//        }
//    }
}