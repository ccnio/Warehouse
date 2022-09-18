package com.ccnio.ware.http

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ccnio.ware.R
import com.ccnio.ware.databinding.ActivityRetrofitBinding
import com.ccnio.ware.http.resp.Repo
import com.ccnio.ware.jetpack.viewbinding.viewBinding
import com.ccnio.ware.utils.getScreenWidth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by ccino on 2021/12/15.
 */
private const val TAG_L = "RetrofitActivity"

class RetrofitActivity : AppCompatActivity(R.layout.activity_retrofit) {
    private val binding by viewBinding(ActivityRetrofitBinding::bind)
    private val viewModel by viewModels<RetrofitVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.normalView.setOnClickListener { normal() }
        binding.coroutineView.setOnClickListener { coroutine() }
    }

    private fun coroutine() {
        viewModel.getRepos("ccnio")
    }

    private fun normal() {
        val retrofit = Retrofit.Builder()//初始化一个Retrofit对象
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(GitHubApiService::class.java) //创建出GitHubApiService对象
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
