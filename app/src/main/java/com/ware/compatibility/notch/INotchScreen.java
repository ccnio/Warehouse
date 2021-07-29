package com.ware.compatibility.notch;

import android.app.Activity;
import android.graphics.Rect;

import java.lang.ref.WeakReference;
import java.util.List;

public interface INotchScreen {

    boolean hasNotch(WeakReference<Activity> activityWeakReference);

    void setDisplayInNotch(WeakReference<Activity> activityWeakReference);

    void getNotchRect(WeakReference<Activity> activity, NotchSizeCallback callback);

    interface NotchSizeCallback {
        void onResult(List<Rect> notchRects);
    }

    interface HasNotchCallback {
        void onResult(boolean hasNotch);
    }

    interface NotchScreenCallback {
        void onResult(NotchScreenInfo notchScreenInfo);
    }

    class NotchScreenInfo {
        public boolean hasNotch;
        public List<Rect> notchRects;
    }
}
