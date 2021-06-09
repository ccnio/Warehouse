package com.ware.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.ware.common.Utils;

@SuppressLint("AppCompatCustomView")
public class ShadowImageView extends ImageView {
    private static final String TAG = "ShadowImageView";
    private RectF mRect;
    private Paint mPaint;

    public ShadowImageView(Context context) {
        super(context);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(Utils.dp2px(5), 0, 0, 0xFFEDEDED);
        mRect = new RectF();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public ShadowImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        mRect.set(0, 0, getWidth(), getHeight());
        Drawable drawable = getDrawable();
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        float v = Utils.dp2px(5);
        float l = (right - actW) / 2f;
        mRect.set(l - v, v, l + actW - v, actH + v);
        Log.d(TAG, "onLayout: " + right + " " + bottom + "  " + actH + " " + Utils.dp2px(200));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mRect, mPaint);
        super.onDraw(canvas);
    }
}
