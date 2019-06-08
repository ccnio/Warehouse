package com.ware.face;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.ware.R;


/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceImageView extends AppCompatImageView {

    private boolean mShowBorder;
    public static final int mWidth = Math.round((DisplayUtil.getScreenWidth() - DisplayUtil.dip2px(50)) / 3f);
    public static final int mHeight = Math.round(mWidth * 1.2f);//1.2 image's height/width
    private Drawable mBorderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.face_cur_bg);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowBorder) {
            mBorderDrawable.setBounds(0, 0, getWidth(), getHeight());
            mBorderDrawable.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = View.MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.getMode(heightMeasureSpec));
//        int widthSpec = View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    public FaceImageView(Context context) {
        this(context, null, 0);
    }

    public FaceImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("FaceImageView", "FaceImageView: " + mWidth + "  " + mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("FaceImageView", "onSizeChanged: " + w + ":" + mWidth + "   " + h + ":" + + mHeight);
    }

    public int getW() {
        return mWidth;
    }

    public int getH() {
        return mHeight;
    }

    public void showBorder(boolean show) {
        mShowBorder = show;
        invalidate();
    }
}
