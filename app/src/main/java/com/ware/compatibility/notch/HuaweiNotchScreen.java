package com.ware.compatibility.notch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.O)
public class HuaweiNotchScreen implements INotchScreen {
    /**
     * 刘海屏全屏显示FLAG
     */
    public static final int FLAG_NOTCH_SUPPORT = 0x00010000;

    /**
     * 设置华为刘海屏手机不使用刘海区
     */
    public static void setNotDisplayInNotch(WeakReference<Activity> activityWeakReference) {
        if (activityWeakReference != null) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                try {
                    Window window = activity.getWindow();
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
                    Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
                    Object layoutParamsExObj = con.newInstance(layoutParams);
                    Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
                    method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
                    window.getWindowManager().updateViewLayout(window.getDecorView(), window.getDecorView().getLayoutParams());
                } catch (Throwable ignore) {
                }
            }
        }
    }

    @Override
    public boolean hasNotch(WeakReference<Activity> activityWeakReference) {
        boolean ret = false;
        if (activityWeakReference != null) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                try {
                    ClassLoader cl = activity.getClassLoader();
                    Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
                    Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
                    ret = (boolean) get.invoke(HwNotchSizeUtil);
                } catch (Throwable ignore) {
                }
            }
        }
        return ret;
    }

    @Override
    public void setDisplayInNotch(WeakReference<Activity> activityWeakReference) {
        if (activityWeakReference != null) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                try {
                    Window window = activity.getWindow();
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
                    Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
                    Object layoutParamsExObj = con.newInstance(layoutParams);
                    Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
                    method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
                    window.getWindowManager().updateViewLayout(window.getDecorView(), window.getDecorView().getLayoutParams());
                } catch (Throwable ignore) {
                }
            }
        }
    }

    @Override
    public void getNotchRect(WeakReference<Activity> activityWeakReference, NotchSizeCallback callback) {
        if (activityWeakReference != null) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                try {
                    ClassLoader cl = activity.getClassLoader();
                    Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
                    Method get = HwNotchSizeUtil.getMethod("getNotchSize");
                    int[] ret = (int[]) get.invoke(HwNotchSizeUtil);
                    ArrayList<Rect> rects = new ArrayList<>();
                    rects.add(ScreenUtil.calculateNotchRect(activity, ret[0], ret[1]));
                    callback.onResult(rects);
                } catch (Throwable ignore) {
                    callback.onResult(null);
                }
            }
        }
    }
}
