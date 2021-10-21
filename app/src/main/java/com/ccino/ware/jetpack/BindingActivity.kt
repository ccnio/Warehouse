package com.ccino.ware.jetpack

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.ware.R
import com.ware.databinding.ActivityBindingBinding
import com.ware.databinding.LayoutStubBinding

private const val TAG = "DataBindingActivity"

/**
 * #
 */
private const val TAG_L = "BindingActivity"

class BindingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBindingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_binding)

        stubLayout()
    }

    private fun stubLayout() {
        binding.stubView.setOnInflateListener { _, inflated ->
            Log.d(TAG_L, "stubLayout: ")
            val stubBinding: LayoutStubBinding = DataBindingUtil.bind(inflated)!!
            stubBinding.stubTextView.text = "Stub inflated"
        }
//        binding.stubView.viewStub?.inflate() or
        binding.stubView.viewStub?.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.set_name_view -> binding.tvName.text = "lisi2"
        }
    }
}
