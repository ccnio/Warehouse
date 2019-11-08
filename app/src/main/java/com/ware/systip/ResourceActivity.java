package com.ware.systip;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ware.R;

public class ResourceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        findViewById(R.id.bt).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
