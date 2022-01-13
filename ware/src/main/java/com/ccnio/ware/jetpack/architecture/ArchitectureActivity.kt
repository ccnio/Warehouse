package com.ccnio.ware.jetpack.architecture

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityArchictureBinding
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG_L = "ArchitectureActivity"

class ArchitectureActivity : AppCompatActivity() {
    private val bind by viewBinding(ActivityArchictureBinding::bind)
    private val viewModel: ArchitectureVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archicture)
        liveDataAndFlow()
        bind.changeStateView.setOnClickListener { viewModel.changeStates() }
    }

    private fun liveDataAndFlow() {
        viewModel.states.observe(this) {
            Log.d(TAG_L, "onCreate: state change: $it")
        }
        viewModel.liveData.observe(this) {
            Log.d(TAG_L, "onCreate: live change: $it")
        }
        lifecycleScope.launch {
            viewModel._states.collect { Log.d(TAG_L, "onCreate: flow: $it") }
        }
        viewModel.liveDataTransform.map { Result(it) }.distinctUntilChanged().observe(this) {
            Log.d(TAG_L, "liveDataAndFlow: $it")
        }
    }
}