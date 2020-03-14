package com.ware.jetpack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.ware.R
import kotlinx.android.synthetic.main.activity_lifecycle2.*

class Fragment1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val liveData = MyLiveData.getInstance()
        liveData?.observe(this, object : Observer<LifeBean> {
            /**
             * onStop后不再调用
             */
            override fun onChanged(t: LifeBean?) {
                Log.d(LifecycleActivity2.TAG, "onChanged fragment: ")
                mDataView.text = t?.mDesc
            }
        })
        return inflater.inflate(R.layout.item_staggered_header, container, false)
    }
}
