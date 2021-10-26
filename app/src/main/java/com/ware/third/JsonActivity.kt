package com.ware.third

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ware.R
import com.ware.databinding.ActivityJsonBinding
import com.ware.jetpack.viewbinding.viewBinding

private const val TAG_L = "JsonActivityL"

class JsonActivity : AppCompatActivity(R.layout.activity_json) {
    private val bind by viewBinding(ActivityJsonBinding::bind)
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.commonView.setOnClickListener { common() }
    }

    private fun common() {
        val list = arrayListOf<String>()
        Log.d(TAG_L, "common: ${gson.toJson(list)}; ${gson.fromJson("[]", Array<String>::class.java).toList()}")
    }


}