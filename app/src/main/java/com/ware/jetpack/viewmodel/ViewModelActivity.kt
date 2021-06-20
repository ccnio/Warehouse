package com.ware.jetpack.viewmodel


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.ware.R
import com.ware.component.BaseActivity
import kotlinx.android.synthetic.main.activity_view_model.*

private const val TAG = "ViewModelActivity"

class ViewModelActivity : BaseActivity(/*R.layout.activity_view_model*/), View.OnClickListener {
    //val mViewModel = ViewModelProvider(this, YourFactoryInstance).get(MViewModel::class.java)
//    val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MViewModel::class.java)
    private val model by viewModels<MViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed { Log.d(TAG, "onCreate: launchWhenResumed ${Thread.currentThread().name}") }//print only one

//        liveDataView.setOnClickListener(this)
        model.article.observe(this, Observer { Log.d(TAG, "onCreate: $it") })
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.liveDataView -> funLiveData()
        }
    }

    private fun funLiveData() {
        model.reqApi()
    }
}
