package com.ccnio.ware.kt

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityFlowBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding

private const val TAG_L = "FlowActivityL"

class FlowActivity : AppCompatActivity(R.layout.activity_flow) {
    private val binding by viewBinding(ActivityFlowBinding::bind)
    private val viewModel by viewModels<FlowViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.stateFlow.observe(this) { Log.d(TAG_L, "stateFlow: $it") }
        binding.flowView.setOnClickListener { flowUse() }
    }

    private fun flowUse() {
        viewModel.doTask()
    }

}
