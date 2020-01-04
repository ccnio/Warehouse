package com.ware.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.ware.R;

public class FourCornerImageView extends ImageView {
    private Path mPath = new Path();
    private int mLeft_top;
    private int mRight_top;
    private int mRight_bottom;
    private int mLeft_bottom;

    public FourCornerImageView(Context context) {
        this(context, null);
    }

    public FourCornerImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourCornerImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FourCornerImageView, defStyleAttr, 0);
        int total = a.getDimensionPixelSize(R.styleable.FourCornerImageView_f_corner_radius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
                getResources().getDisplayMetrics()));// 默认为10DP
        mLeft_top = a.getDimensionPixelSize(R.styleable.FourCornerImageView_left_top_radius, total);
        mRight_top = a.getDimensionPixelSize(R.styleable.FourCornerImageView_right_top_radius, total);
        mRight_bottom = a.getDimensionPixelSize(R.styleable.FourCornerImageView_right_bottom_radius, total);
        mLeft_bottom = a.getDimensionPixelSize(R.styleable.FourCornerImageView_left_bottom_radius, total);

        a.recycle();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
        mPath.addRoundRect(new RectF(0, 0, w, h), new float[]{mLeft_top, mLeft_top, mRight_top, mRight_top, mLeft_bottom, mLeft_bottom, mRight_bottom, mRight_bottom}, Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        super.onDraw(canvas);
        canvas.restore();

    }
}
