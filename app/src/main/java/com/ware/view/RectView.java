package com.ware.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.edreamoon.Utils;
import com.ware.R;

/**
 * Created by jianfeng.li on 2017/12/18.
 */

public class RectView extends View {

    private int mMargin;
    private int mRealW;
    private Rect mRectBounds;
    private NinePatchDrawable mDrawable;
    private int mScreenWidth;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawable.draw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mScreenWidth, mRealW);
    }

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMargin = (int) Utils.dp2px(44);
        mScreenWidth = Utils.getScreenWidth();
        mRealW = mScreenWidth - mMargin * 2;
        mRectBounds = new Rect(mMargin, 0, mMargin + mRealW, mRealW);

        mDrawable = (NinePatchDrawable) Utils.getDrawableByID(R.drawable.camera_line);
        mDrawable.setBounds(mRectBounds);
    }
}
