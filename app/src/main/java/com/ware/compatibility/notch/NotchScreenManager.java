package com.ware.compatibility.notch;

import android.app.Activity;
import android.os.Build;
import java.lang.ref.WeakReference;

public class NotchScreenManager {

    private static final NotchScreenManager instance = new NotchScreenManager();
    private final INotchScreen notchScreen;

    private NotchScreenManager() {
        notchScreen = getNotchScreen();
    }

    public static NotchScreenManager getInstance() {
        return instance;
    }

    public void setDisplayInNotch(Activity activity) {
        if (notchScreen != null) {
            notchScreen.setDisplayInNotch(new WeakReference<>(activity));
        }
    }

    public void getNotchInfo(final WeakReference<Activity> activity, final INotchScreen.NotchScreenCallback notchScreenCallback) {
        final INotchScreen.NotchScreenInfo notchScreenInfo = new INotchScreen.NotchScreenInfo();
        if (notchScreen != null && notchScreen.hasNotch(activity)) {
            notchScreen.getNotchRect(activity, notchRects -> {
                if (notchRects != null && notchRects.size() > 0) {
                    notchScreenInfo.hasNotch = true;
                    notchScreenInfo.notchRects = notchRects;
                }
                notchScreenCallback.onResult(notchScreenInfo);
            });
        } else {
            notchScreenCallback.onResult(notchScreenInfo);
        }
    }

    private INotchScreen getNotchScreen() {
        INotchScreen notchScreen = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            notchScreen = new AndroidPNotchScreen();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (RomUtils.isHuawei()) {
                notchScreen = new HuaweiNotchScreen();
            } else if (RomUtils.isOppo()) {
                notchScreen = new OppoNotchScreen();
            } else if (RomUtils.isVivo()) {
                notchScreen = new VivoNotchScreen();
            } else if (RomUtils.isXiaomi()) {
                notchScreen = new MiNotchScreen();
            }
        }
        return notchScreen;
    }

}
