package com.ware.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class LibraryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
    }

    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, LibraryActivity::class.java))
        }
    }
}