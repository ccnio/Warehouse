package com.ware.systip.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ware.R;

public class RecyclerTouchActivity extends AppCompatActivity {

    private static final String TAG = "AcActivity";
    public static final int REQ_SEC = 0x2;

    private Context mAppCtx = RecyclerTouchActivity.this;
    private RecyclerView mRecyclerView;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_touch);

        Log.d(TAG, Color.parseColor("#1a000000") + " : ");
//        Log.d(TAG, Color.parseColor("0xff00CCFF") + "");

        findViewById(R.id.test).setBackgroundColor(Color.parseColor("#1a000000"));

        mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new LeafPagerAdapter(getSupportFragmentManager()));
    }
}
