package com.edreamoon.stu.touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.edreamoon.stu.tool.Tool;

/**
 * Created by jianfeng.li on 2018/1/3.
 */

public class PointerImg extends AppCompatImageView {

    private int mWidth;
    private int mHeight;
    private Matrix mMatrix;
    private int mScreenH;
    private int mScreenW;
    private int mDrawH;
    private int mDrawW;
    private float mStartX;
    private float mStartY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction(); //前8位代表pointerIndex，后8位代表事件类型
        int actionMasked = action & MotionEvent.ACTION_MASK;
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                Log.e("lijf", "onTouchEvent: ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("lijf", "onTouchEvent: ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                Log.e("lijf", "onTouchEvent: ACTION_MOVE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e("lijf", "onTouchEvent: ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e("lijf", "onTouchEvent: ACTION_POINTER_UP");
                break;

            case MotionEvent.ACTION_CANCEL:
                Log.e("lijf", "onTouchEvent: ACTION_CANCEL");
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mMatrix = getMatrix();
        Drawable drawable = getDrawable();
        mDrawH = drawable.getIntrinsicHeight();
        mDrawW = drawable.getIntrinsicWidth();
        mMatrix.postTranslate((mWidth - mDrawW) / 2f, (mHeight - mDrawH) / 2f);
        setImageMatrix(mMatrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("lijf", "onDraw: ");
    }

    public PointerImg(Context context) {
        this(context, null);
    }

    public PointerImg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerImg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenH = Tool.getScreenHeight();
        mScreenW = Tool.getScreenWidth();
    }
}
