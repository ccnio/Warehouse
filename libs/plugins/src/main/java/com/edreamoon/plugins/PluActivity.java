package com.ware.plugins;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PluActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plu_activity_plu);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PluActivity.class);
        context.startActivity(intent);
    }
}