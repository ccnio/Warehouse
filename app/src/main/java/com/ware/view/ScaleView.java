package com.ware.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.ware.R;
import com.ware.common.Utils;


/**
 * Created by jianfeng.li on 2017/12/18.
 */

public class ScaleView extends ImageView {
    private final GestureDetector mGestureDetector;
    private final int mScreenW;
    private final float mRealW;
    private final float[] mValues = new float[9];
    private Matrix mMatrix;//= new Matrix();
    public static final String TAG = "ScaleView";

    private ScaleGestureDetector mScaleGesture;
    private int mWidth;
    private int mHeight;
    private Drawable mDrawable;
    private int mIntrinsicH;
    private int mIntrinsicW;
    private float mScaleX;
    private float mScaleY;
    private float mScale;
    private float mSideMargin;
    private Rect mCropRect;
    private float mMarginTop;
    private int mReqWidth;
    private int mReqHeight = 1080;
    private Paint mSrcPaint;
    private Paint mDstPaint;
    private PorterDuffXfermode mXfermode;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mReqHeight = mHeight = h;
        mMarginTop = (mHeight - mRealW) / 2;

        mDrawable = getDrawable();
        mIntrinsicH = getDrawable().getIntrinsicHeight();
        mIntrinsicW = getDrawable().getIntrinsicWidth();
        mScaleX = mRealW / mIntrinsicW;
        mScaleY = mRealW / mIntrinsicH;

        mScale = mScaleX > mScaleY ? mScaleX : mScaleY;


        if (mIntrinsicH < mIntrinsicW) {
            mMatrix.postScale(mScale, mScale);
            float transY = (mIntrinsicW * mScale - mWidth) / 2.0f;
            mMatrix.postTranslate(-transY, mMarginTop);
        } else {
            mMatrix.postScale(mScale, mScale);
            float scaleTop = mMarginTop - (mIntrinsicH * mScale - mRealW) / 2;
            mMatrix.postTranslate(mSideMargin, scaleTop);

        }

        mCropRect.set((int) mSideMargin, (int) mMarginTop, (int) (mSideMargin + mRealW), (int) (mMarginTop + mRealW - Utils.dp2px(1)));
        setImageMatrix(mMatrix);
    }


    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mReqWidth = mScreenW = Utils.getScreenWidth();
        mSideMargin = Utils.dp2px(44) + Utils.dp2px(4);
        mRealW = mScreenW - mSideMargin * 2;
        mMatrix = getImageMatrix();
        mMatrix.getValues(mValues);

        mCropRect = new Rect();
        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mSrcPaint.setColor(Utils.getColorById(R.color.camera_color));

        mScaleGesture = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                float scaleFactor = detector.getScaleFactor();
                float scale = getScale();
                Log.e(TAG, "onScale2222: " + getScale() + "  " + scaleFactor);
                if (scaleFactor > 1 || scale > mScale && scaleFactor < 1) {
                    if (scaleFactor * scale < mScale) {
                        scaleFactor = mScale / scale;
                    }
                    mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                    setImageMatrix(mMatrix);
                }
                return true;
            }
        });
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                mMatrix.postTranslate(-distanceX, -distanceY);
                validateSide();
                setImageMatrix(mMatrix);
                return true;
            }
        });
    }

    private void validateSide() {
        RectF rect = getMatrixRectF(mMatrix);
        float deltaX = 0;
        float deltaY = 0;
        int width = mWidth;
        if (rect.left > mSideMargin) {
            deltaX = -rect.left + mSideMargin;
        } else if (rect.right < width - mSideMargin) {
            deltaX = width - mSideMargin - rect.right;
        }
        if (rect.top > mMarginTop) {
            deltaY = -rect.top + mMarginTop;
        } else if (rect.bottom < mMarginTop + mRealW) {
            deltaY = mMarginTop + mRealW - rect.bottom;
        }
        mMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     */
    private RectF getMatrixRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        matrix.mapRect(rect);
        return rect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return mScaleGesture.onTouchEvent(event);
    }

    private float getScale() {
        mMatrix.getValues(mValues);
        float v = mValues[Matrix.MSCALE_X];
        return v;
    }

    private float getTransX() {
        mMatrix.getValues(mValues);
        float v = mValues[Matrix.MTRANS_X];
        return v;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(0, 0, mWidth, mHeight, mSrcPaint);
        mDstPaint.setColor(Color.GREEN);
        mDstPaint.setXfermode(mXfermode);
        canvas.drawRect(mCropRect, mDstPaint);
        mDstPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setImage(String path) {
//        this.image = image;
//        mMatrix.postScale(mhe,1f);
//        Bitmap image = getFitSampleBitmap(path, mReqWidth, mReqHeight);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pointer);
        setImageBitmap(bitmap);
    }

    /**
     * 获取剪切图
     */
    public Bitmap crop() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap cropBitmap = null;
        try {
            cropBitmap = Bitmap.createBitmap(getDrawingCache(), mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());
//            zoomedCropBitmap = zoomBitmap(cropBitmap, 200, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        destroyDrawingCache();
        return cropBitmap;
    }

    public static Bitmap getFitSampleBitmap(String file_path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file_path, options);
        options.inSampleSize = getFitInSampleSize(width, height, options);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file_path, options);
    }

    public static int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int inSampleSize = 1;
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        if (outWidth > outHeight) {
            int temp = reqWidth;
            reqWidth = reqHeight;
            reqHeight = temp;
        }
        if (outWidth > reqWidth || outHeight > reqHeight) {
            float widthRatio = outWidth / (float) reqWidth;
            float heightRatio = outHeight / (float) reqHeight;
            inSampleSize = Math.round(Math.min(widthRatio, heightRatio));
        }
        return inSampleSize;
    }
}