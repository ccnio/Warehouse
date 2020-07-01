package com.ware.jetpack.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ware.common.BaseViewModel
import com.ware.http.HttpHelper
import com.ware.http.resp.FeedArticle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by jianfeng.li on 20-7-1.
 */
private const val TAG = "MViewModel"

class MViewModel : BaseViewModel() {
    val article = MutableLiveData<FeedArticle>()
    fun reqApi() {
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) { HttpHelper.getFeedArticleListCoroutine() }
            Log.d(TAG, "reqApi: article = ${res.data?.size}   ${Thread.currentThread().name}")
            article.value = res
        }
    }

    /**
     * 查看文档可以看到其局限性
     */
    fun reqApi2() {
        val liveData = liveData(Dispatchers.IO) {
            val article = HttpHelper.getFeedArticleListCoroutine()
            emit(article)
        }
    }

}