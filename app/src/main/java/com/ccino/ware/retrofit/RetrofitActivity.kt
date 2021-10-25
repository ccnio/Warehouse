package com.ccino.ware.retrofit

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.databinding.ActivityRetrofitBinding
import com.ware.http.HttpHelper
import com.ware.http.base.BaseObserver
import com.ware.http.resp.FeedArticle
import com.ware.jetpack.viewbinding.viewBinding
import com.ware.tool.io2Main
import kotlinx.android.synthetic.main.activity_retrofit.*
import kotlinx.coroutines.launch

private const val TAG_L = "RetrofitActivity"

/**
 * # 集成协程时 接口调用是在 Dispatcher.io 子协程上执行，不影响协程操作
 */
class RetrofitActivity : BaseActivity(), View.OnClickListener {
    private val binding by viewBinding(ActivityRetrofitBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        binding.gsonView.setOnClickListener(this)
        rxBt.setOnClickListener(this)
        coroutineBt.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.gsonView -> gsonNullField()
            R.id.rxBt -> rx()
            R.id.coroutineBt -> coroutine()
        }
    }

    private fun coroutine() {
        val ktService = KtService.create()
        launch {
            val friendLink = ktService.getFriendLink()
            Log.d(TAG_L, "coroutine: $friendLink")
        }
    }

    private fun rx() {
        addDisposable(HttpHelper.getFeedArticleListRx().compose(io2Main()).subscribeWith(object : BaseObserver<FeedArticle>() {
            override fun onSuccess(t: FeedArticle) {
                Log.d(TAG_L, "onSuccess: ${t.data?.size}")
            }

            override fun onFail(code: Int, msg: String?) {
                Log.d(TAG_L, "onFail: code = $code; msg = $msg")
            }

        }))
    }

    private fun gsonNullField() {
        val gson = Gson()

        val toJson = gson.toJson(Bean("li", null))
        Log.d(TAG_L, "gsonNullField: $toJson")
    }

    data class Bean(val name: String, val age: Int? = null)
}