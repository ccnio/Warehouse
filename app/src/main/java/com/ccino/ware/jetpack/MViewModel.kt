package com.ccino.ware.jetpack

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.ware.common.BaseViewModel
import com.ware.http.HttpHelper
import com.ware.http.resp.FeedArticle
import kotlinx.coroutines.*

/**
 * Created by jianfeng.li on 20-7-1.
 */
private const val TAG_L = "MViewModel"

class MViewModel : BaseViewModel() {
    val article = MutableLiveData<FeedArticle>()
    val myLiveData = MyLiveData()
    fun reqApi() {
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) { HttpHelper.getFeedArticleListCoroutine() }
            Log.d(TAG_L, "reqApi: article = ${res.data?.size}   ${Thread.currentThread().name}")
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

    private var tasks: Job? = null
    fun startTask() {
        tasks = viewModelScope.launch {
            val task1 = async(Dispatchers.IO) {
                Log.d(TAG_L, "startTask: task1 before")
                delay(3000)
                Log.d(TAG_L, "startTask: task1")
                "task1"
            }

            val task2 = async(Dispatchers.IO) {
                Log.d(TAG_L, "startTask: task2 before")
                delay(5000)
                Log.d(TAG_L, "startTask: task2")
                "task2"
            }
            Log.d(TAG_L, "startTask result: ${task1.await()} --- ${task2.await()}")
        }
    }

    fun cancelTask() {
//        viewModelScope.cancel() or
        tasks?.cancel()
    }

}