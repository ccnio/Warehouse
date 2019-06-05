package com.ware.systip;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//import com.edreamoon.plugins.PluActivity;
import com.ware.R;

public class ResourceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        findViewById(R.id.bt).setOnClickListener(this);
    }

    /**
     * @return 改变color值的透明度;做渐变时用
     */
    public int changeAlpha(int color, float ratio) {
        int alpha = Color.alpha(color);
        int newAlpha = Math.round(alpha * ratio);

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(newAlpha, r, g, b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                /**
                 * 检测 图片资源冲突
                 */
//                PluActivity.start(ResourceActivity.this);
                break;
        }
    }
}
