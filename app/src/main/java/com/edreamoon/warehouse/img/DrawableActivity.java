package com.edreamoon.warehouse.img;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edreamoon.warehouse.R;

public class DrawableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);
        View commonView = findViewById(R.id.common);

        new MDrawable.Builder(commonView)
                .setBgColor(Color.GREEN)
                .setCornerRadius(10F)
                .build();

        new MDrawable.Builder(findViewById(R.id.circle))
                .setBgColor(0xfff2ef22)
                .setShape(MDrawable.Builder.CIRCLE)
                .setShadowColor(Color.DKGRAY)
                .setShadowRadius(10)
                .setShadowDx(10)
                .setShadowDy(10)
                .build();
    }
}
