package com.ware.face;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.squareup.picasso.Transformation;

/**
 * Created by jianfeng.li on 19-6-6.
 */
public class FaceRoundTrans implements Transformation {
    private final int mHorMargin;
    private final int mRadius;
    private final int mVerMargin;

    public FaceRoundTrans(int radius, int verMargin, int horMargin) {
        mRadius = radius;
        mVerMargin = verMargin;
        mHorMargin = horMargin;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        drawRoundRect(canvas, paint, width, height);
        source.recycle();

        return bitmap;
    }

    private void drawRoundRect(Canvas canvas, Paint paint, float width, float height) {
        float right = width - mHorMargin;
        float bottom = height - mVerMargin;
        RectF rect = new RectF(mHorMargin, mVerMargin, right, bottom);
        Log.d("FaceRoundTrans", "drawRoundRect: " + rect.toShortString() + "   " + mRadius);
        canvas.drawRoundRect(rect, mRadius, mRadius, paint);
    }

    @Override
    public String key() {
        return "RoundTransformation(radius=" + mRadius + ", margin=" + mVerMargin + ")";
    }
}

