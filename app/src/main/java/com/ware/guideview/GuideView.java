package com.ware.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

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
    private final PorterDuffXfermode mXfermode;
    private final Paint mLightPaint;
    private GuideBuilder builder;
    private Path mPath;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        builder.mHostView.getGlobalVisibleRect(rect);
        RectF rectF = new RectF(rect);
        mPath.addRect(rectF, Path.Direction.CW);

        Rect rect2 = new Rect();
        mRootView.getGlobalVisibleRect(rect2);

        /**
         * 高亮处理1
         */
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(rect2, mPaint);
        mLightPaint.setXfermode(mXfermode);
        canvas.drawRect(rect, mLightPaint);
        mLightPaint.setXfermode(null);
        canvas.restoreToCount(layerId);

        /**
         *  高亮处理2:无法在paint上做阴影处理
         */
//        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
//        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
//        canvas.drawRect(rect2, mPaint);
//        canvas.restoreToCount(layerId);
    }

    GuideView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0x99000000);

        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL));
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mActivity = (Activity) context;
        mRootView = mActivity.getWindow().getDecorView();
        mPath = new Path();
    }

    private static final String TAG = "GuideView";

    public void setBuilder(GuideBuilder builder) {
        this.builder = builder;
    }
}
