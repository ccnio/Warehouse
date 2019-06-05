package com.ware.touch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ware.R;

public class PointerActivity extends AppCompatActivity {

    private PointerImg mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointer);

        mImgView = findViewById(R.id.img);
//        mImgView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
                mImgView.setImageResource(R.drawable.pointer);
//            }
//        }, 1000);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PointerActivity.class);
        context.startActivity(intent);
    }
}
