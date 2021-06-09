package com.ware.jetpack.binding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityDataBindBinding
import com.ware.tool.binding

private const val TAG = "DataBindingActivity"

/**
 * # view bind
 *  XML 文件都生成一个对应的绑定类，每个绑定类会包含根视图以及具有 ID 的所有视图的引用。绑定类的命名是：将 XML 文件的名称转换为驼峰命名，并在末尾添加 “Binding” 。view的命名也是如此
 */
class DataBindingActivity : AppCompatActivity(R.layout.activity_data_bind) {
    private lateinit var binding: ActivityDataBindBinding
    private val binding2 by binding(ActivityDataBindBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataBindBinding.inflate(layoutInflater)
        binding.tvHello.text = "hi"
        binding2.tvHello.text = "world"
    }
}
