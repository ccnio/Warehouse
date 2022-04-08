package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityKotlinBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.ccnio.ware.utils.intent
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val TAG_L = "KotlinActivity"

class KotlinActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityKotlinBinding::bind)
    private val delegateIntent by intent<String>("key")
    private val delegateIntentDefault by intent("key") {
        "default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
        funAsParam()
        bind.delegateView.setOnClickListener { DelegateDemo().delegate() }
        Log.d(TAG_L, "delegateIntent: $delegateIntent, $delegateIntentDefault")
        bind.operatorView.setOnClickListener { OperatorDemo().case() }
    }


    private fun funAsParam() {
        "abc".receiver {
            Log.d(TAG_L, "funAsParam: ${isNullOrBlank()}")
        }
    }

    private inline fun <T> T.receiver(block: T.() -> Unit): T {
        block()
        return this
    }
}

