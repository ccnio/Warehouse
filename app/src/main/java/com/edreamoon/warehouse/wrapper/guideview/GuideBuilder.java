package com.edreamoon.warehouse.wrapper.guideview;

import android.app.Activity;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by jianfeng.li on 2018/1/25.
 */

public class GuideBuilder {
    public final Activity mActivity;
    public View mHostView;
    public static final String TAG = "GuideBuilder";
    public int[] mLocation;

    public GuideBuilder(Activity activity) {
        mActivity = activity;
    }

    public GuideBuilder with(@IdRes int hostId) {
        mHostView = mActivity.findViewById(hostId);
        return this;
    }

    public void show() {
        GuideView guideView = new GuideView(mActivity);


        guideView.setBuilder(this);
        FrameLayout root = (FrameLayout) mActivity.getWindow().getDecorView();//FrameLayout
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(guideView, params);
    }
}
