package com.ware.widget.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by jianfeng.li on 19-6-24.
 */
public class ViewTest extends View {
    public ViewTest(Context context) {
        this(context, null, 0);
    }

    public ViewTest(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
