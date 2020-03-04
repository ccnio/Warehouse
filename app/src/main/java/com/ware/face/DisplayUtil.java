package com.ware.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

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

    public static int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    public static int getScreenWidth() {
        return screenWidth;
    }
}
