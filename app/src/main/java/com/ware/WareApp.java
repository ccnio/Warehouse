package com.ware;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.ware.common.Utils;

//import com.facebook.stetho.Stetho;
//import com.facebook.stetho.okhttp3.StethoInterceptor;

/**
 * Created by jianfeng.li on 2017/12/29.
 */

public class WareApp extends Application {

    public static Context sContext;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d("WareApp", "attachBaseContext: ");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WareApp", "onCreate: ");
        sContext = getApplicationContext();
        Utils.init(getApplicationContext());
        Stetho.initializeWithDefaults(this);
//        Stetho.initializeWithDefaults(this);
//        new OkHttpClient.Builder()
//                .addNetworkInterceptor(new StethoInterceptor())
//                .build();
    }

}
