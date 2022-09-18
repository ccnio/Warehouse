package com.ccnio.ware.http

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ware.common.BaseViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by jianfeng.li on 2022/9/18.
 */
private const val TAG = "RetrofitVM"

class RetrofitVM : BaseViewModel() {
    private val api = Retrofit.Builder()//初始化一个Retrofit对象
        .baseUrl("https://api.gisfthub.com/")
//        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(GitHubApiService::class.java) //创建出GitHubApiService对象

    fun getRepos(user: String) {
        val ex = CoroutineExceptionHandler { context, throwable ->
            Log.d(TAG, "getRepos: $throwable")
        }
        viewModelScope.launch(ex) {
            val list = api.getRepos(user)
            Log.d(TAG, "getRepos: $list")
        }


        launchOnUI {

        }.catch {

        }
    }

    private fun launchOnUI(block: suspend CoroutineScope.() -> Unit):  CoroutineScope{
        val ex = CoroutineExceptionHandler { context, throwable ->
            Log.d(TAG, "getRepos: $throwable")
        }
        viewModelScope.launch(ex){

        }
        return viewModelScope
    }

    fun CoroutineScope.catch(block: () -> Unit): CoroutineScope {
        viewModelScope.coroutineContext
        return viewModelScope
    }
}