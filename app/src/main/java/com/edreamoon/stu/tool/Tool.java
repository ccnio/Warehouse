package com.edreamoon.stu.tool;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.edreamoon.stu.MApp;

/**
 * Created by jianfeng.li on 2018/1/3.
 */

public class Tool {
    public static Context mContext = MApp.mContext;

    public static int getScreenWidth() {
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
