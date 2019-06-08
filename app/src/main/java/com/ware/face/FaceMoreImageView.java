package com.ware.face;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.ware.R;


/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceMoreImageView extends AppCompatImageView {
    private Drawable mBorderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.face_cur_bg);

    public FaceMoreImageView(Context context) {
        this(context, null, 0);
    }

    public FaceMoreImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceMoreImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        Log.d("FaceMoreImageView", "FaceImageView: " + mWidth + "  " + mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("FaceMoreImageView", "onSizeChanged: " + w + ":" + DisplayUtil.dip2px(300) + "   " + h);
    }
}
