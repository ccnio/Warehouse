package com.ware.media

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityBitmapBinding
import com.ware.jetpack.viewbinding.viewBinding

private const val TAG = "BitmapActivityL"

/**
 * # mutable
 * - 虽然使用的同一个 drawable 资源，但两者实例不是同一个, 却又共享状态。
 */
class BitmapActivity : AppCompatActivity(R.layout.activity_bitmap), View.OnClickListener {
    private val binding by viewBinding(ActivityBitmapBinding::bind)

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mutableView -> mutableView()
        }
    }

    private fun mutableView() {
        val res1 = getDrawable(R.drawable.mutable_bg)
        val res2 = getDrawable(R.drawable.mutable_bg)
        //res1 != res2
        Log.d(TAG, "mutableView: res1 = $res1; res2 = $res2")

        val drawable1 = binding.editableView.background as GradientDrawable
        val drawable2 = binding.standardView.background as GradientDrawable
        val state1 = drawable1.constantState
        val state2 = drawable2.constantState
        //drawable1 != drawable2; state1 = state2
        Log.d(TAG, "mutableView drawable1 = $drawable1; drawable2 = $drawable2; state1 = $state1; state2 = $state2")
        drawable1.mutate()
        //state1 变了，state2 未变
        Log.d(TAG, "mutableView after mutable: state1 = ${drawable1.constantState}; state2 = ${drawable2.constantState}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mutableView.setOnClickListener(this)

    }
}
