package com.ware.third.retrofit

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "RetrofitActivity"

class RetrofitActivity : BaseActivity(), View.OnClickListener {
    private val scope = MainScope()
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
        scope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val article = HttpHelper.getFeedArticleListCoroutine()
                    Log.d(TAG, "coroutine: $article")
                } catch (e: Exception) {
                    Log.d(TAG, "coroutine exception: $e")
                }

            }
        }
    }

    private fun rx() {
        addDisposable(HttpHelper.getFeedArticleListRx().compose(io2Main()).subscribeWith(object : BaseObserver<FeedArticle>() {
            override fun onSuccess(t: FeedArticle) {
                Log.d(TAG, "onSuccess: ${t.data?.size}")
            }

            override fun onFail(code: Int, msg: String?) {
                Log.d(TAG, "onFail: code = $code; msg = $msg")
            }

        }))
    }

    private fun common() {
        HttpHelper.getFeedArticleList().enqueue(object : Callback<FeedArticle> {
            override fun onFailure(call: Call<FeedArticle>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<FeedArticle>, response: Response<FeedArticle>) {
                Log.d(TAG, "onResponse: ${response.body()}")
            }
        })
    }
}