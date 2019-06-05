package com.ware.jetpack

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.ware.R
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
        val liveData = MyLiveData.getInstance()
        mChangeView.setOnClickListener {
            //            liveData.updateData()
            val intent = Intent(this, LifecycleActivity2::class.java)
            startActivity(intent)
        }

        liveData?.observe(this, object : Observer<LifeBean> {
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
