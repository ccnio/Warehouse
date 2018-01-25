package com.edreamoon.stu.wrapper.guideview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jianfeng.li on 2018/1/12.
 */

/**
 * 实现内容
 *
 * 1.使用方式代码植入
 * 2.显示区域有圆形、矩形
 * 3.提示内容支持drawable、layout
 * 4.支持位移偏差，箭头控制
 */
public class GuideView extends View {
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0x80000000);
    }

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private static final String TAG = "GuideView";

    public GuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
