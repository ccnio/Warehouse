package com.ware.systip.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ware.face.DisplayUtil;

public class DividerView extends RelativeLayout {
    public DividerView(Context context) {
        super(context);
    }

    public DividerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DividerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("DividerView", "onSizeChanged: " + w + ":" + h + "     " + DisplayUtil.dip2px(100));
    }
}
