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

import com.edreamoon.stu.tool.Utils;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction(); //前8位代表pointerIndex，后8位代表事件类型
        int actionMasked = action & MotionEvent.ACTION_MASK;//得到事件类型 或者通过 event.getActionMasked()

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                int index1 = event.getActionIndex();
                pointerId1 = event.getPointerId(index1);
                Log.e("lijf", "pointer 1: " + "index1 = " + index1 + "; id1 = " + pointerId1);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int index2 = event.getActionIndex();
                pointerId2 = event.getPointerId(index2);
                Log.e("lijf", "pointer 2: " + "index1 = " + index2 + "; id2 = " + pointerId2);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int actionIndex = event.getActionIndex();
                int index = event.findPointerIndex(pointerId1);
                Log.e("lijf", "pointer 1: " + index + " " + actionIndex);
                break;
//            case MotionEvent.ACTION_CANCEL:
//                Log.e("lijf", "onTouchEvent: ACTION_CANCEL");
//                break;
//            case MotionEvent.ACTION_UP:
//                int pointerCount = event.getPointerCount();
//                for (int i = 0; i < pointerCount; i++) {
//                    Log.e("lijf", "onTouchEvent: " + event.getPointerId(i));
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("lijf", "onTouchEvent: ACTION_MOVE");
//                break;
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
        mScreenH = Utils.getScreenHeight();
        mScreenW = Utils.getScreenWidth();
    }
}
