package com.ware;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainTestActivity extends AppCompatActivity {

    private ViewFlipper mFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
        mFlipper = findViewById(R.id.flipper);


        View view = View.inflate(this, R.layout.flipper_item, null);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("abcd");
        View view2 = View.inflate(this, R.layout.flipper_item, null);
        TextView tv2 = view2.findViewById(R.id.tv);
        tv2.setText("我们不一样");
        mFlipper.addView(view);
        mFlipper.addView(view2);
    }
}
