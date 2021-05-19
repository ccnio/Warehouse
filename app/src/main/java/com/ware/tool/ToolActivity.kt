package com.ware.tool

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import com.ware.R
import com.ware.common.ApiSource
import kotlinx.android.synthetic.main.activity_persist.*

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
class ToolActivity : AppCompatActivity(), View.OnClickListener {
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
        setContentView(R.layout.activity_persist)
        sourceView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sourceView -> sourceSet()
        }
    }
}