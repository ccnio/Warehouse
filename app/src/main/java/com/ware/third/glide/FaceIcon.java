package com.ware.third.glide;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.ware.face.DisplayUtil;


/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceIcon extends AppCompatImageView {
    public static final String TAG = "FaceIcon";
    private int size = DisplayUtil.dip2px(100);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        int widthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightSpec);
    }

    public FaceIcon(Context context) {
        this(context, null, 0);
    }

    public FaceIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
