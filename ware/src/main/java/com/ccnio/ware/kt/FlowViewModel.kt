package com.ccnio.ware.kt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ccnio.ware.http.GitHubApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by jianfeng.li on 2021/8/24.
 */
class DataRet(var name: String? = null) {
    override fun toString(): String {
        return "DataRet(name=$name)"
    }
}

private const val TAG_L = "FlowViewModel"

class FlowViewModel : ViewModel() {
    private val data = DataRet()
    private val _stateFlow = MutableSharedFlow<DataRet>()
    val stateFlow = _stateFlow.asLiveData()
    private val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val service = retrofit.create(GitHubApiService::class.java) //创建出GitHubApiService对象

    fun doTask() {
//        viewModelScope.launch {
//            flow {
//                data.name = "result"
//                val repos = viewModelScope.async { service.getRepos("ccnio") } //返回一个 Call 对象
//                val repos2 = viewModelScope.async { service.getRepos("edreamoon") }  //返回一个 Call 对象
//                repos.await()
//                repos2.await()
//
//                emit(data)
//            }.onStart {
//                data.name = "start"
//                _stateFlow.emit(data)
//            }.catch {
//                Log.e(TAG_L, "doTask: exception ${it.message}")
//                data.name = "error"
//                _stateFlow.emit(data)
//            }.collect {
//                _stateFlow.emit(it)
//            }
//        }
        viewModelScope.launch {
            try {
                data.name = "result"
//                val repos = async { service.getRepos("ccnio") } //返回一个 Call 对象
//                val repos2 = async { service.getRepos("edreamoon") }  //返回一个 Call 对象
//                repos.await()
//                repos2.await()
                1/0
                _stateFlow.emit(data)
            } catch (e: Exception) {
                Log.e(TAG_L, "doTask: $e")
            }

        }
    }
}