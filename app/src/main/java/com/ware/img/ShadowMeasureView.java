package com.ware.img;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.ware.common.Utils;

public class ShadowMeasureView extends View {

    private final int mWidth;
    private final Rect mRect;
    private Paint mPaint;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        canvas.drawRect(mRect, mPaint);
    }

    public ShadowMeasureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mWidth = (int) Utils.dp2px(100);
        int radius = (int) Utils.dp2px(5);
        mRect = new Rect(radius, radius, mWidth, mWidth -radius*3);
        mPaint.setShadowLayer(radius, 0, 0, Color.BLACK);
    }

}
