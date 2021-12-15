package com.ccnio.ware.http

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityRetrofitBinding
import com.ware.jetpack.viewbinding.viewBinding

/**
 * Created by ccino on 2021/12/15.
 */
class RetrofitActivity : AppCompatActivity(R.layout.activity_retrofit) {
    private val binding by viewBinding(ActivityRetrofitBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.normalView.setOnClickListener { normal() }

    }

    private fun normal() {

    }
}
