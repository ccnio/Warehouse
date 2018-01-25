package com.edreamoon.stu.wrapper.guideview;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by jianfeng.li on 2018/1/25.
 */

public class GuideBuilder {
    private final Activity mActivity;
    private int mHostViewId;

    public GuideBuilder(Activity activity) {
        mActivity = activity;
    }

    public GuideBuilder with(int viewId) {
        mHostViewId = viewId;
        return this;
    }

    public GuideBuilder with(int xOff,int yOff){
        return this;
    }

    public void show(){
        GuideView guideView = new GuideView(mActivity);
        FrameLayout root = mActivity.findViewById(android.R.id.content);//FrameLayout
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(guideView, params);
    }
}
