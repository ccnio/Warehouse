package com.edreamoon.stu.systip;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.edreamoon.stu.R;

public class ResourceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
    }

    /**
     * @return 改变color值的透明度;做渐变时用
     */
    public int changeAlpha(int color, float ratio) {
        int alpha = Color.alpha(color);
        int newAlpha = Math.round(alpha * ratio);

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(newAlpha, r, g, b);
    }
}
