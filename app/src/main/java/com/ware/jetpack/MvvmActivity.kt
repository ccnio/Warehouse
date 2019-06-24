package com.ware.jetpack


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ware.R
import com.ware.common.BaseActivity
import kotlinx.android.synthetic.main.activity_mvvm.*

class MvvmActivity : BaseActivity(), View.OnClickListener, Observer<CompositeData> {
    override fun onChanged(t: CompositeData?) {
        Log.d("MvvmActivity", "onChanged: ")
    }

    private val mViewModel by lazy { ViewModelProviders.of(this).get(MViewModel::class.java) }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mButton -> {
                mViewModel.requestFeed()
            }
            R.id.mButton2 -> {
                mViewModel.requestSite()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvvm)
        mViewModel.mCompositeLiveData.observe(this, this)
        mButton.setOnClickListener(this)
    }
}
