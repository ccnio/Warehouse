package com.ware.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ware.WareApp;

public class DisplayUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static Drawable getDrawable(Context context, int drawableId) {
        Resources resources = context.getResources();
        Drawable drawable = resources.getDrawable(drawableId);
        return drawable;
    }

    public static int getStatusBarHeight() {
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? WareApp.sContext.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    public static int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    @Deprecated
    public static int getScreenWidth() {
        return screenWidth;
    }

    @Deprecated
    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenHeightPx(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (windowManager != null) {
//            windowManager.getDefaultDisplay().getMetrics(dm);
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.heightPixels;
        }
        return 0;
    }

    public static int getScreenWidthPx(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (windowManager != null) {
//            windowManager.getDefaultDisplay().getMetrics(dm);
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.widthPixels;
        }
        return 0;
    }

}
