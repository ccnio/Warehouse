package com.ccnio.ware.http

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityRetrofitBinding
import com.ccnio.ware.http.adapter.CustomCallAdapterFactory
import com.ccnio.ware.http.resp.Repo
import com.ccnio.ware.http.resp.StateResult
import com.ccnio.ware.http.state.StateCallAdapterFactory
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


/**
 * Created by ccino on 2021/12/15.
 */
private const val TAG_L = "RetrofitActivity"

class RetrofitActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityRetrofitBinding::bind)
    private val viewModel by viewModels<RetrofitVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrofit)
        binding.normalView.setOnClickListener { normal() }
        binding.coroutineView.setOnClickListener { coroutine() }
        binding.adapterView.setOnClickListener { adapter() }
        binding.libView.setOnClickListener { library() }
    }

    private fun library() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .addCallAdapterFactory(StateCallAdapterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WanApiService::class.java)
        lifecycleScope.launch {
            val call = service.getStateProjectTree()
//            when(call) {
//                is StateResult.Exception -> TODO()
//                is StateResult.NetError -> TODO()
//                is StateResult.ServerError -> TODO()
//                is StateResult.Success -> {
//                    Log.d(TAG_L, "library: ${call.result}")
//                }
//            }
            Log.d(TAG_L, "adapter: $call")
        }

    }

    private fun adapter() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            //使用addCallAdapterFactory向Retrofit注册CustomCallAdapterFactory
            .addCallAdapterFactory(CustomCallAdapterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WanApiService::class.java)
        thread { Log.d(TAG_L, "adapter: ${service.getProjectTree().getBodyData()}") }
    }

    private fun coroutine() {
        viewModel.getRepos("ccnio")
    }

    private fun normal() {
        val retrofit = Retrofit.Builder()//初始化一个Retrofit对象
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(StateCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WanApiService::class.java) //创建出GitHubApiService对象
        val repos = service.listRepos("ccnio")  //返回一个 Call 对象
        repos.enqueue(object : Callback<List<Repo>> { //调用 enqueue 方法在回调方法里处理结果
            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.e(TAG_L, "onFailure: ${t.message}")
            }

            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.d(TAG_L, "onResponse: $response")
            }
        })
    }
}
