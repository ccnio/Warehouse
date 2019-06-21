package com.ware.touch;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.ware.common.Utils;

/**
 * Created by jianfeng.li on 2018/1/3.
 */

public class PointerImg extends AppCompatImageView {

    private static final String TAG = "PointerImg";
    private int mWidth;
    private int mHeight;
    private Matrix mMatrix;
    private int mScreenH;
    private int mScreenW;
    private int mDrawH;
    private int mDrawW;
    private float mStartX;
    private float mStartY;
    private int pointerId2;
    private int pointerId1;
    private boolean mDragged;
    private float mPreDist;
    private ScaleGestureDetector mScaleGesture;
    private GestureDetector mGestureDetector;

    private float caluateDis(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);
        return mScaleGesture.onTouchEvent(event);//事件传给ScaleGestureDetector
        /** 自己处理事件
         int action = event.getAction(); //前8位代表pointerIndex，后8位代表事件类型
         int actionMasked = action & MotionEvent.ACTION_MASK;//得到事件类型 或者通过 event.getActionMasked()
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index = event.getActionIndex();
                pointerId2 = event.getPointerId(index);
                mDragged = true; //标记处于缩放模式
                mPreDist = caluateDis(mStartX, mStartY, event.getX(index), event.getY(index));//计算两点距离
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mDragged = event.getPointerCount() > 2 ? true : false;
                break;
            case MotionEvent.ACTION_UP:
                mDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();//getX() pointerIndex为0
                float y = event.getY();
                if (mDragged) {
                    int index2 = event.findPointerIndex(pointerId2);
                    float x2 = event.getX(index2);
                    float y2 = event.getY(index2);

                    float focusX = (x + x2) / 2;//缩放中心两点中心
                    float focusY = (y2 + y) / 2;

                    float curDist = caluateDis(x, y, x2, y2);
                    float scale = curDist / mPreDist;//计算缩放比例
                    mMatrix.postScale(scale, scale, focusX, focusY);
                    mPreDist = curDist;

                    mStartX = x;
                    mStartY = y;
                    setImageMatrix(mMatrix);
                }
                break;
        }
        return true;
         **/
    }



    public PointerImg(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenH = Utils.getScreenHeight();
        mScreenW = Utils.getScreenWidth();

        mScaleGesture = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                mMatrix.postScale(factor, factor, detector.getFocusX(), detector.getFocusY());
                return true;
            }
        });

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mMatrix.postTranslate(-distanceX, -distanceY);
                setImageMatrix(mMatrix);
                return true;
            }
        });
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

    public PointerImg(Context context) {
        this(context, null);
    }

    public PointerImg(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
}
