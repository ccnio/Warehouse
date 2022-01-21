package com.ccino.store

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.business.export.IApi
//import com.ccnio.business.export.api
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG_L = "StoreActivity"

@AndroidEntryPoint
class StoreActivity : AppCompatActivity() {
//    @Inject lateinit var iapi: IApi
    val p = People.POLICE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_strore)
        Log.d(TAG_L, "onCreate: api1 = api2 = ,$p")
    }
}