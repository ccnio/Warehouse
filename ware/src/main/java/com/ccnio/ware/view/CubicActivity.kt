package com.ccnio.ware.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityWidgetBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding


class CubicActivity : AppCompatActivity(R.layout.activity_cubic) {
    private val binding by viewBinding(ActivityWidgetBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
