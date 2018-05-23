package com.edreamoon.warehouse.img;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.edreamoon.warehouse.R;


public class DrawableActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DrawableActivity";
    private boolean flag = true;
    private MDrawableStateView mStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);

        /**
         * 圆角、圆形、阴影
         */
        new ShapeDrawable.Builder(findViewById(R.id.circle))
                .setBgColor(0xfff2ef22)
                .setShape(ShapeDrawable.Builder.CIRCLE)
                .setShadowColor(Color.DKGRAY)
                .setShadowRadius(5)
                .setShadowDx(2) //dx、dy 不能大于 shadowRadius
                .setShadowDy(2)
                .build();


        /**
         * 自定义view 状态
         */
        mStateView = findViewById(R.id.state_drawable);
        mStateView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.state_drawable) {
            mStateView.setState(flag ? MDrawableStateView.TWO : MDrawableStateView.ONE);
            flag = !flag;
        }
        Log.d(TAG, "onClick: ");
    }
}
