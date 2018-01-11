package com.edreamoon.stu;

import android.app.Application;
import android.content.Context;

/**
 * Created by jianfeng.li on 2017/12/29.
 */

public class MApp extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

}
