package com.ware.face;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jianfeng.li on 19-6-6.
 */
public class FaceProgressView extends View {

    private int mHeight = FaceImageView.mHeight;
    private int mWidth = FaceImageView.mWidth;
    private Paint mPaint;
    private float mRate = 1f;
    private int mRadius = DisplayUtil.dip2px(16.7f);
    private RectF mRect = new RectF(0, 0, mWidth, mHeight);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("FaceProgressView", "onMeasure: " + mWidth + "  " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.clipRect(0f, mHeight - mRate * mHeight, mWidth, mHeight);
        canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);
        canvas.restore();
    }

    public FaceProgressView(Context context) {
        this(context, null, 0);
    }

    public FaceProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0x7f000000);
    }

    public void setProgress(int progress) {
        mRate = progress / 100f;
        invalidate();
    }
}
