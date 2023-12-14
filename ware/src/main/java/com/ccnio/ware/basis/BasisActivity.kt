package com.ccnio.ware.basis

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityBasisBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ArrayBlockingQueue

private const val TAG = "BasisActivity"

class BasisActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityBasisBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basis)
        enum()
        bind.proxyView.setOnClickListener { ProxyCase().dynamicProxy() }
        blockProduce()
        bind.blockGetView.setOnClickListener { blockGet() }

        lifecycleScope.launch{
            while (true) {
                delay(2000)
                Log.d(TAG, "onCreate: delay")
            }
        }
    }


    private val blockingDeque = ArrayBlockingQueue<Int>(2)
    private var num = 0
    private fun blockProduce() {
//        while (true) {
            val add = blockingDeque.put(num++)
//        blockingDeque.
            Log.d(TAG, "blockProduce: add=$add, num=$num")
//        }
    }

    private fun blockGet() {
        val poll = blockingDeque.poll()
        Log.d(TAG, "blockGet: $poll")
    }


    private fun enum() {
        Log.d(TAG, "enum: ${EnumAnimal.CAT}")
    }
}