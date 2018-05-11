package com.edreamoon.warehouse.img;

import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edreamoon.Utils;
import com.edreamoon.warehouse.R;

public class DrawableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable);

//        View id = findViewById(R.id.hum);
//        StateListDrawable background = (StateListDrawable) id.getBackground();
//        background.addState(new int[]{android.R.attr.state_pressed}, Utils.getDrawableByID(R.drawable.run_day_bg));
//        background.addState(new int[]{android.R.attr.state_}, Utils.getDrawableByID(R.drawable.run_lab_bg));
//        id.setBackgroundDrawable(background);
//        id.setBackgroundDrawable(new MJStateDrawable(R.drawable.run_lab_bg, StatePressed.DARK));
//        findViewById(R.id.tempContainer).setBackgroundDrawable(new MJStateDrawable(R.drawable.run_lab_bg, StatePressed.DARK));
//        findViewById(R.id.hum).setBackgroundDrawable(new MJStateDrawable(R.drawable.run_lab_bg, StatePressed.DARK));
//        findViewById(R.id.wind).setBackgroundDrawable(new MJStateDrawable(R.drawable.run_lab_bg, StatePressed.DARK));
//        findViewById(R.id.air).setBackgroundDrawable(new MJStateDrawable(R.drawable.run_lab_bg, StatePressed.DARK));
    }
}
