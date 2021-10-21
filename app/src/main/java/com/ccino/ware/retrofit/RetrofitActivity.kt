package com.ccino.ware.retrofit

import android.os.Bundle
import android.util.Log
import android.view.View
import com.ware.R
import com.ware.component.BaseActivity
import com.ware.http.HttpHelper
import com.ware.http.base.BaseObserver
import com.ware.http.resp.FeedArticle
import com.ware.tool.io2Main
import kotlinx.android.synthetic.main.activity_retrofit.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG_L = "RetrofitActivity"

/**
 * # Api 调用接口的方法是在 Dispatcher.io 子协程上执行，所以不需要切换线程
 */
class RetrofitActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        commonBt.setOnClickListener(this)
        rxBt.setOnClickListener(this)
        coroutineBt.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.commonBt -> common()
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

    private fun common() {
        HttpHelper.getFeedArticleList().enqueue(object : Callback<FeedArticle> {
            override fun onFailure(call: Call<FeedArticle>, t: Throwable) {
                Log.d(TAG_L, "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<FeedArticle>, response: Response<FeedArticle>) {
                Log.d(TAG_L, "onResponse: ${response.body()}")
            }
        })
    }
}