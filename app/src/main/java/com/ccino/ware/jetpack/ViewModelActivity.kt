package com.ccino.ware.jetpack


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.databinding.ActivityViewmodelBinding
import com.ware.jetpack.viewbinding.viewBinding

private const val TAG = "ViewModelActivity"

// 顶层函数版本
inline fun <reified T : ViewModel> getViewModel(owner: ViewModelStoreOwner, configLiveData: T.() -> Unit = {}): T =
    ViewModelProvider(owner)[T::class.java].apply { configLiveData() }

// 扩展函数版本
inline fun <reified T : ViewModel> ViewModelStoreOwner.getSelfViewModel(configLiveData: T.() -> Unit = {}): T =
    getViewModel(this, configLiveData)

private const val TAG_L = "ViewModelActivityL"

class ViewModelActivity : BaseActivity(R.layout.activity_viewmodel), View.OnClickListener {
    //val mViewModel = ViewModelProvider(this, YourFactoryInstance).get(MViewModel::class.java)
//    val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MViewModel::class.java)
//    private val model by viewModels<MViewModel>()
//    private val model by lazy { getViewModel<MViewModel>(this, { article.observe(this@ViewModelActivity, { Log.d(TAG, ": ") }) }) }
    private val binding by viewBinding(ActivityViewmodelBinding::bind)
    private val viewModel by lazy { getSelfViewModel<MViewModel> { article.observe(this@ViewModelActivity, { Log.d(TAG, ": ") }) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenResumed { Log.d(TAG, "onCreate: launchWhenResumed ${Thread.currentThread().name}") }//print only one
        binding.cancelView.setOnClickListener { cancelScope() }
        binding.startTaskView.setOnClickListener { startTask() }
//        liveDataView.setOnClickListener(this)
        viewModel.article.observe(this, Observer { Log.d(TAG, "onCreate: $it") })
        binding.liveDataView.setOnClickListener { viewModel.myLiveData.observe(this) { Log.d(TAG_L, "onCreate: myLiveData ") } }
    }

    private fun startTask() {
        viewModel.startTask()
    }

    private fun cancelScope() {
        viewModel.cancelTask()
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.liveDataView -> funLiveData()
        }
    }

    private fun funLiveData() {
        viewModel.reqApi()
    }
}
