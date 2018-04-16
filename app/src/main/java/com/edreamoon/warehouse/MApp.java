package com.edreamoon.warehouse;

import android.app.Application;
import android.content.Context;

import com.edreamoon.Utils;

/**
 * Created by jianfeng.li on 2017/12/29.
 */

public class MApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(getApplicationContext());
    }

}
