package com.edreamoon;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.OrientationEventListener;

/**
 * Created by jianfeng.li on 2018/1/3.
 */

public class Utils {
    public static Context mContext;
    // Orientation hysteresis amount used in rounding, in degrees
    public static final int ORIENTATION_HYSTERESIS = 5;
    private static Resources mResource;
    public static void init(Context context){
        mContext = context;
       mResource = mContext.getResources();
    }

    public static int getScreenWidth() {
        DisplayMetrics dm = mResource.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics dm = mResource.getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float getDensity() {
        DisplayMetrics metrics = mResource.getDisplayMetrics();
        return metrics.density;
    }

    private static int mStatusBarHeight;

    public static int getStatusBarHeight() {
        if(mStatusBarHeight !=0) return mStatusBarHeight;
        int resourceId = mResource.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            mStatusBarHeight = mResource.getDimensionPixelSize(resourceId);
        }
        return mStatusBarHeight;
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
