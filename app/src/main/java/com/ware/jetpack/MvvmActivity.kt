package com.ware.jetpack


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_mvvm.*

class MvvmActivity : BaseActivity(), View.OnClickListener, Observer<LifeBean> {
    override fun onChanged(t: LifeBean?) {
        Log.d("MViewModel", "onChanged: ")
    }
//    override fun onChanged(t: CompositeData?) {
//        Log.d("MViewModel", "onChanged: ")
//    }

    //val mViewModel = ViewModelProvider(this, YourFactoryInstance).get(MViewModel::class.java)
//    val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MViewModel::class.java)
    private val mViewModel = ViewModelProvider(this).get(MViewModel::class.java)


    private val mHandler = Handler()

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
//        mViewModel.mCompositeLiveData.observe(this, this)
        mViewModel.myLiveData.observe(this, this)
        mButton.setOnClickListener(this)
//        mViewModel.testLeak2(this)
//        mViewModel.testLeak(this)
        mViewModel.testLeak3()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MViewModel", "onDestroy: ")
    }
}
