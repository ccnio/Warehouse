package com.edreamoon.stu.img;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.edreamoon.stu.R;
import com.edreamoon.stu.tool.Utils;

public class BitmapActivity extends AppCompatActivity {

    private static final String TAG = "BitmapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);

        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.bit);
        int byteCount = bitmap.getByteCount();
        Log.e("lijf", ": " + byteCount);

        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        String bitmapConfig = bitmap.getConfig().toString();
        Log.e("lijf", "bitmapHeight = " + bitmapHeight + ";  bitmapWidth = " + bitmapWidth + "; config = " + bitmapConfig);
        //打印结果：bitmapHeight = 700;  bitmapWidth = 481; config = ARGB_8888

        Log.e("lijf", "density = " + Utils.getDensity());
        //图片内存占用 计算来源
    }
}
