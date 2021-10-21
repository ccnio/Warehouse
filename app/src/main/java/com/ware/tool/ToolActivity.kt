package com.ware.tool

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import com.ware.R
import com.ware.common.ApiSource
import kotlinx.android.synthetic.main.activity_tools.*

private const val TAG = "PersistActivity"

/**
 * # sourceSets
 * ## sourceSets {
 * dev.java.srcDirs = ['src/main/java', 'src/dev/java']
 * google.java.srcDirs = ['src/main/java', 'src/google/java']
 * }
 * 对flavor有要求,如果只在应用的非main module配置"dev,google" flavor是编译不过的,要求所有module都需要有对应的flavor,
 * 所以可在项目下的build.gradle下使用gradle语法统一配置,免去麻烦.在打包时,所有module的flavor跟随main module,然后配置[flavor]Implementation即可实现不同实现.
 * ## 或者使用两个module,类/方法签名完全一致,不同实现
 *
 */
class ToolActivity : AppCompatActivity(R.layout.activity_tools), View.OnClickListener {
    data class Bean(val start: Int?, val end: Int)

    private fun sourceSet() {
        /*
        sourceSets {
            dev.java.srcDirs = ['src/main/java', 'src/dev/java']
            google.java.srcDirs = ['src/main/java', 'src/google/java']
        }
         */
        ApiSource().testFrom()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootDir = MMKV.initialize(this)
        Log.d(TAG, "onCreate: dir = $rootDir")
        sourceView.setOnClickListener(this)
        timerStart.setOnClickListener(this)
        timerPause.setOnClickListener(this)

        val toJson = Gson().toJson(Bean(null, 22))
        Log.d(TAG, "onCreate: $toJson")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sourceView -> sourceSet()
            R.id.timerStart -> timerStart()
            R.id.timerPause -> timerPause()
        }
    }

    private val timer = Timer(3000)
    private fun timerStart() {
        timer.start()
        timer.emiter.observe(this, { Log.d(TAG, "timer: $it") })
    }

    private fun timerPause() {
        timer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}