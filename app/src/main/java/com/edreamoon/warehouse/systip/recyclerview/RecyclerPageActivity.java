package com.edreamoon.warehouse.systip.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.edreamoon.warehouse.R;

import java.util.ArrayList;


public class RecyclerPageActivity extends AppCompatActivity {
    private static final String TAG = "RecyclerPageActivity";
    private RecyclerView mRecyclerView;
    private int mPreScrollY;
    private float mPrey;
    private boolean mMoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            strings.add("pos: " + i);
        }
        StaggeredAdapter adapter = new StaggeredAdapter(strings);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(adapter.mListener);


        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        mPrey = event.getY();
//                        Log.d(TAG, "onTouch   down : " + mPrey);
//                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (!mMoved) {
                            mPrey = event.getY();
                            mMoved = true;
                        }
                        Log.d(TAG, "onTouch   move : " + mPrey);
                        break;
                    case MotionEvent.ACTION_UP:
                        mMoved = false;
                        Log.d(TAG, "onTouch: " + ((event.getY() - mPrey) < 0) + "  " + event.getY());
                        break;
                }
                return false;
            }
        });

    }

}
