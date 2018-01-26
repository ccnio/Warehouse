package com.edreamoon.stu.wrapper.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.edreamoon.stu.tool.Utils;

/**
 * Created by jianfeng.li on 2018/1/12.
 */

/**
 * 实现内容
 * <p>
 * 1.使用方式代码植入
 * 2.显示区域有圆形、矩形
 * 3.提示内容支持drawable、layout
 * 4.支持位移偏差，箭头控制
 */
public class GuideView extends View {
    private final Paint mPaint;
    private final Activity mActivity;
    private final View mRootView;
    private final Paint mCirclePaint;
    private GuideBuilder builder;
    private Path mPath;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);

        Rect rect = new Rect();
        builder.mHostView.getGlobalVisibleRect(rect);
        Log.i(TAG, "onDraw target: " + rect.toShortString());

        Rect rect2 = new Rect();
        mRootView.getGlobalVisibleRect(rect2);
        Log.i(TAG, "onDraw content: " + rect2.toShortString() + "  " + mRootView.getHeight());

        Log.i(TAG, "onDraw: " + "   " + Utils.getStatusBarHeight() + "  " + Utils.getScreenHeight());

        mPaint.setColor(Color.RED);
        canvas.save();
        mPath.addCircle(400, 600, 150, Path.Direction.CW);
//        canvas.clipPath(mPath, Region.Op.INTERSECT);
        canvas.drawPath(mPath,mCirclePaint);
        canvas.drawRect(rect2, mPaint);
        canvas.restore();
    }

    GuideView(Context context) {
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.GREEN);
        mActivity = (Activity) context;
        mRootView = mActivity.getWindow().getDecorView();
        mPath = new Path();

    }

    private static final String TAG = "GuideView";

    public void setBuilder(GuideBuilder builder) {
        this.builder = builder;
    }
}
