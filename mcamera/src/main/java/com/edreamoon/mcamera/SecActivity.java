package com.ware.mcamera;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SecActivity.class);
        context.startActivity(intent);
    }
}
