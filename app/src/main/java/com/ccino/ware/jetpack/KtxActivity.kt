package com.ccino.ware.jetpack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ware.R
import com.ware.databinding.ActivityJetpackKtxBinding
import com.ware.jetpack.viewbinding.viewBinding
import com.ware.systip.SecActivity

private const val TAG = "KtxActivityL"

/**
 * # Activity Result API。用于替代 startActivityForResult() 和 onActivityResult() 的工具
 */
class KtxActivity : AppCompatActivity(), View.OnClickListener {
    private val binding by viewBinding(ActivityJetpackKtxBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jetpack_ktx)
        binding.activityResultView.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activityResultView -> activityResult()
        }
    }

    /**
     * ActivityResultContract 协议类定义生成结果所需的输入类型以及结果的输出类型，Activity Result API 已经提供了很多默认的协议类，实现请求权限、拍照等常见操作。
     * 还可以自定义协议类，继承 ActivityResultContract
     */
    private val contentContracts = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> Log.d(TAG, "activityResult: $uri") }
    private val activityContracts = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri -> Log.d(TAG, "activityResult: $uri") }
    private fun activityResult() {
        contentContracts.launch("image/*") //选择文件
        activityContracts.launch(Intent(this, SecActivity::class.java))//启动界面
    }
}
