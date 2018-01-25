package com.edreamoon.stu.wrapper.guideview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.edreamoon.stu.R

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        GuideBuilder(this).with(R.id.view1).show()
    }
}
