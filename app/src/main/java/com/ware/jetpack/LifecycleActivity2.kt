package com.ware.jetpack

import androidx.lifecycle.Observer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.ware.R
import kotlinx.android.synthetic.main.activity_lifecycle2.*

/**
 * Lifecycle ： 与Activity和Fragment的生命周期有关
 */
class LifecycleActivity2 : AppCompatActivity() {
    var id = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lifecycle2)
        val fragment1 = Fragment1()

        //lifecycle
        getLifecycle().addObserver(LifecyclePresenter(lifecycle))


        //live data
        mOpView.setOnClickListener {
            if (!id) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.mFragment, fragment1)
                transaction.commit()
            } else {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.remove(fragment1)
                transaction.commit()
            }
            id = !id
        }

        val liveData = MyLiveData.getInstance()
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
