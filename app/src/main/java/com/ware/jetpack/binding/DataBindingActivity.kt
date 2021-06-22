package com.ware.jetpack.binding

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityCaseBinding
import com.ware.jetpack.viewbinding.viewBinding

private const val TAG = "DataBindingActivity"

/**
 * # view bind
 *  XML 文件都生成一个对应的绑定类，每个绑定类会包含根视图以及具有 ID 的所有视图的引用。绑定类的命名是：将 XML 文件的名称转换为驼峰命名，并在末尾添加 “Binding” 。view的命名也是如此
 */
class DataBindingActivity : AppCompatActivity(R.layout.activity_case), View.OnClickListener {
    private val binding by viewBinding(ActivityCaseBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView<ActivityCaseBinding>(this, R.layout.activity_case)
        binding.setNameView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.set_name_view -> binding.tvName.text = "lisi2"
        }
    }
}
