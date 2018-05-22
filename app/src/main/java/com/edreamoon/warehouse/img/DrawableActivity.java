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

        MDrawable drawable = new MDrawable.Builder()
                .setBgColor(Color.GREEN)
                .setCornerRadius(4F)
                .build();
//        MDrawable commonDrawable = new MDrawable(mBgColor);
//        commonDrawable.bindView(commonView);
        commonView.setBackground(drawable);
    }
}
