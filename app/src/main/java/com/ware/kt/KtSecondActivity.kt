package com.ware.kt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.ware.R

class KtSecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt_second)

        if (intent?.getStringExtra(SHARE_CONFIG) == null) {
            Log.d(TAG, " null: ")
        }
    }

    companion object {
        //        private const val CITY_ID = "city_id"
        private const val TAG = "MainShareActivity"
        private const val SHARE_CONFIG = "share_config"

        fun start(context: Context, data: String) {
            val intent = Intent(context, KtSecondActivity::class.java)
//            intent.putExtra(SHARE_CONFIG, data)
            context.startActivity(intent)
        }
    }

}
