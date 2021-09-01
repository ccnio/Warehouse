package com.ware.jetpack.hilt

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ware.R
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * https://juejin.cn/post/6902009428633698312
 * # HiltAndroidApp
 * 所有使用 Hilt 的应用都必须包含一个带有 @HiltAndroidApp 注释的 Application 类。它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项。
 * # Android 类注入依赖
 * @AndroidEntryPoint 标明要使用注入的类
 * @Inject 标明要注入的成员
 * @Inject 标明注入的成员如何构造
 */
private const val TAG = "HiltActivity"

@AndroidEntryPoint
class HiltActivity : AppCompatActivity() {
    @Inject lateinit var truck: Truck
    @Inject lateinit var httpClient: OkHttpClient
    @Inject lateinit var retrofit: Retrofit
    @Inject lateinit var user: User
    @Inject lateinit var user2: User2
    @Inject lateinit var user3: User3
    private val viewModel by lazy { ViewModelProvider(this).get(HiltViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilt)
        truck.deliver()
        Log.d(TAG, "onCreate: httpClient = $httpClient; retrofit = $retrofit")
        viewModel.genData()

        Log.d(TAG, "onCreate: user = $user")
        Log.d(TAG, "onCreate: user2 = $user2")
        Log.d(TAG, "onCreate: user3 = $user3")
    }
}
