package com.ware.jetpack.viewmodel


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.ware.component.BaseActivity

private const val TAG = "ViewModelActivity"

// 顶层函数版本
inline fun <reified T : ViewModel> getViewModel(owner: ViewModelStoreOwner, configLiveData: T.() -> Unit = {}): T =
    ViewModelProvider(owner)[T::class.java].apply { configLiveData() }

// 扩展函数版本
inline fun <reified T : ViewModel> ViewModelStoreOwner.getSelfViewModel(configLiveData: T.() -> Unit = {}): T =
    getViewModel(this, configLiveData)

class ViewModelActivity : BaseActivity(/*R.layout.activity_view_model*/), View.OnClickListener {
    //val mViewModel = ViewModelProvider(this, YourFactoryInstance).get(MViewModel::class.java)
//    val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MViewModel::class.java)
//    private val model by viewModels<MViewModel>()
//    private val model by lazy { getViewModel<MViewModel>(this, { article.observe(this@ViewModelActivity, { Log.d(TAG, ": ") }) }) }
    private val model by lazy { getSelfViewModel<MViewModel> { article.observe(this@ViewModelActivity, { Log.d(TAG, ": ") }) } }

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
