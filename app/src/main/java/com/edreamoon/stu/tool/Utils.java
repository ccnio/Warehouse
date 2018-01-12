package com.edreamoon.stu.tool;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;

import com.edreamoon.stu.MApp;

/**
 * Created by jianfeng.li on 2018/1/3.
 */

public class Utils {
    public static Context mContext = MApp.mContext;
    // Orientation hysteresis amount used in rounding, in degrees
    public static final int ORIENTATION_HYSTERESIS = 5;

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

    /**
     * @param orientation
     * @param orientationHistory
     * @return 返回屏幕旋转的角度 0、90、180，270
     */
    public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min(dist, 360 - dist);
            changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }
}
