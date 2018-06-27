package com.edreamoon.warehouse.jetpack

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.edreamoon.warehouse.R
import kotlinx.android.synthetic.main.activity_lifecycle.*

/**
 * Lifecycle ： 与Activity和Fragment的生命周期有关
 */
class LifecycleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle)

        //lifecycle
        getLifecycle().addObserver(LifecyclePresenter(lifecycle))


        //live data
        val liveData = MyLiveData()
        mChangeView.setOnClickListener {
            liveData.updateData()
        }

        liveData.observe(this, object : Observer<LifeBean> {
            /**
             * onStop后不再调用
             */
            override fun onChanged(t: LifeBean?) {
                Log.d(TAG, "onChanged: ")
                mDataView.text = t?.mDesc
            }
        })
    }

    companion object {
        const val TAG = "LifecycleActivity"
    }
}
