package com.ware.widget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AttrActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_activity_attr)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AttrActivity::class.java)
            context.startActivity(intent)
        }
    }
}
