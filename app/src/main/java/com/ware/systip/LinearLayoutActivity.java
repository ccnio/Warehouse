package com.ware.systip;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ware.R;

/**
 * 设置分隔线
 *
 * 分割线如果是图片那就直接使用图片就行
 * 如果要使用颜色就必须使用shape来显示，直接使用颜色或Color是没有用的
 * 使用shape的时候要注意设置size属性不设置宽高分割线就不会显示出来
 */

public class LinearLayoutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linearlayout);
    }
}
