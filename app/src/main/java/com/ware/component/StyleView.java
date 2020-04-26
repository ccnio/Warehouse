package com.ware.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.ware.R;

/**
 * Created by jianfeng.li on 20-4-24.
 * 1. get style value in code
 */
public class StyleView extends View {
    private static final String TAG = "StyleView";
    private final Paint paint = new Paint();

    public StyleView(Context context) {
        this(context, null, 0);
    }

    public StyleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Log.d(TAG, "StyleView: " + R.font.heavy);
    }

    public StyleView(Context context, @Nullable AttributeSet attr, int defStyleAttr) {
        super(context, attr, defStyleAttr);
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

//
//        // The attributes you want retrieved
//        int[] attrs = {R.attr.myFont};
//        // Parse MyCustomStyle, using Context.obtainStyledAttributes()
//        TypedArray ta = context.obtainStyledAttributes(R.style.FontStyle, attrs);
//        int resourceId = ta.getResourceId(0, 0);
//        Log.d(TAG, "StyleView: get attr = " + resourceId);
//        // Fetch the text from your style like this.
//        //String text = ta.getString(2);
//        ta.recycle();
//
//        //android.content.res.Resources$NotFoundException: Resource ID #0x0 : if style font is null
//        Typeface typeface = ResourcesCompat.getFont(context, resourceId);
//        paint.setTypeface(typeface);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("ABCDE", 55, 45, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(600, 100);
    }
}
