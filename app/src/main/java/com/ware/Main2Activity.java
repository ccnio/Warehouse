package com.ware;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.ware.R;

/**
 * Created by jianfeng.li on 2017/11/24.
 */

public class Main2Activity extends AppCompatActivity {
    public static String TAG = Main2Activity.class.getName();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.mRecyclerView);

    }

}