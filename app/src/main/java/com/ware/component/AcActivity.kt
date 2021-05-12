package com.ware.component

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ware.R
import com.ware.jetpack.MViewModel
import com.ware.kt.KtActivity
import com.ware.systip.SecActivity
import kotlinx.android.synthetic.main.activity_launch_mode.*

/**
启动模式
例子是在一个activity里启动自己，查看生命周期的调用。
- standard: 每次都会调用onCreate(),并且任务栈里有多个此activity的实例
- singleTop:每次调用startActivity()不会再创建activity实例，生命周期也不会回调，会回调onNewIntent()
## 后台startActivity的操作都将会延迟几秒
## Q及后　在后台启动Activity的限制
# ActivityResultContract--替代startActivityForResult: https://juejin.im/post/5e80cb1ee51d45471654fae7
新的 Activity Result API，我们还可以单独的类中处理结果回调，真正做到 单一职责 。通过 ActivityResultRegistry 来完成的，ComponentActivity 中包含了一个 ActivityResultRegistry 对象
 */
private const val TAG = "AcActivity"

/**
 * # exported属性: 适用于四大组件.exported默认值: 如果包含有intent-filter 默认值为true; 没有intent-filter  exported默认值为false.
 *   target android 12, 如果有intent-filter将会强制指明exported,否则install failed.
 */
class AcActivity : BaseActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.mLaunchModeView -> {
                start(this)
            }
            R.id.mBgAcView -> {
                startActivityBackground()
            }
            R.id.mWakeLockView -> {
                mWakeLockView.postDelayed({ com.ware.systip.start() }, 1000 * 10)
            }
            R.id.goSecView -> {
                startForRet()
            }
        }
    }

    /**
     * 除了 StartActivityForResult 之外，官方还默认提供了 RequestPermissions ，Dial ，RequestPermission ，TakePicture ，它们都是 ActivityResultContract 的实现类。
     * 除了使用官方默认提供的这些之外，我们还可以自己实现 ActivityResultContract。
     */
    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        Log.d("AcActivity", activityResult.toString())//ActivityResult{resultCode=RESULT_CANCELED, data=null}
    }

    private fun startForRet() {
        val intent = Intent(this, SecActivity::class.java)
        launcher.launch(intent)
    }

    private fun startActivityBackground() {
        mBgAcView.postDelayed({
            Log.d("AcActivity", "startActivityBackground: ${System.currentTimeMillis()}")
            startActivity(Intent(this@AcActivity, KtActivity::class.java))
        }, 3000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_mode)
        mLaunchModeView.setOnClickListener(this)
        mBgAcView.setOnClickListener(this)
        mWakeLockView.setOnClickListener(this)
        goSecView.setOnClickListener(this)
        Log.d("AcActivity", "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        Log.d("AcActivity", "onStart: ")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("AcActivity", "onNewIntent: ${intent?.action}")
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, AcActivity::class.java))
        }
    }
}
