package com.ccnio.ware.basis

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityBasisBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding

private const val TAG_L = "BasisActivity"

class BasisActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityBasisBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basis)
        enum()
        bind.proxyView.setOnClickListener { ProxyCase().dynamicProxy() }
    }


    private fun enum() {
        Log.d(TAG_L, "enum: ${EnumAnimal.CAT}")
    }
}