package com.edreamoon.stu.media;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;

import com.edreamoon.Utils;
import com.edreamoon.stu.R;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private OrientationListener mOrientationListener;
    private int mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mOrientationListener = new OrientationListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationListener.enable();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationListener.disable();
    }

    /**
     * 监测设备旋转
     */
    private class OrientationListener extends OrientationEventListener {
        public OrientationListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == ORIENTATION_UNKNOWN) return;
            mOrientation = Utils.roundOrientation(orientation, mOrientation);
            Log.d(TAG, "onOrientationChanged: " + orientation + "   " + mOrientation);
//            mCurrentModule.onOrientationChanged(orientation);

            /**
             * 强制设置屏幕方向
             */
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
