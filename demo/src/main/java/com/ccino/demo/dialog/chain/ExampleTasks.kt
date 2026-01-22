package com.ccino.demo.dialog.chain

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleOwner

/**
 * 示例：需要异步请求数据的广告弹窗
 */
class AdTask : DialogTask(priority = 50, id = "ad_task") {

    override fun prepare(host: LifecycleOwner, activity: ComponentActivity, callback: (Boolean) -> Unit) {
        // 模拟 2秒的网络请求延迟
        Handler(Looper.getMainLooper()).postDelayed({
            // 假设请求结果：需要显示
            val shouldShowAd = true
            callback(shouldShowAd)
        }, 2000)
    }

    override fun onShow(host: LifecycleOwner, activity: ComponentActivity, chain: ChainCallback) {
        AlertDialog.Builder(activity)
            .setTitle("广告")
            .setMessage("这是一个异步加载后的广告（优先级=50）")
            .setPositiveButton("查看") { _, _ -> 
                // 业务逻辑...
            }
            .setNegativeButton("关闭") { _, _ -> 
                // 业务逻辑...
            }
            .setOnDismissListener {
                chain.next()
            }
            .show()
    }
}

/**
 * 示例：高优先级通知
 */
class NoticeTask : DialogTask(priority = 100, id = "notice_task") {
    override fun onShow(host: LifecycleOwner, activity: ComponentActivity, chain: ChainCallback) {
        Toast.makeText(activity, "高优先级通知: 正在执行（优先级=100）", Toast.LENGTH_SHORT).show()
        
        AlertDialog.Builder(activity)
            .setTitle("重要通知")
            .setMessage("点击'中断'将停止后续所有弹窗\n点击'继续'将显示下一个弹窗")
            .setPositiveButton("继续") { _, _ -> 
                // 正常逻辑
            }
            .setNegativeButton("中断") { _, _ ->
                // 演示中断逻辑：调用 stop() 会清空队列，后续的 AdTask 不会再显示
                chain.stop()
            }
            .setOnDismissListener {
                // 注意：这里需要根据业务判断调用 next 还是 stop
                // 简单起见，如果上面没调 stop，这里默认 next。
                // 实际上如果上面调了 stop，由于 AtomicBoolean 保护，这里的 next 会被忽略，是安全的。
                chain.next()
            }
            .show()
    }
}

/**
 * 示例：普通优先级的更新提示
 */
class UpdateTask : DialogTask(priority = 30, id = "update_task") {
    override fun onShow(host: LifecycleOwner, activity: ComponentActivity, chain: ChainCallback) {
        AlertDialog.Builder(activity)
            .setTitle("版本更新")
            .setMessage("发现新版本 v2.0.0\n是否立即更新？（优先级=30）")
            .setPositiveButton("更新") { _, _ ->
                Toast.makeText(activity, "开始更新...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("稍后") { _, _ -> }
            .setOnDismissListener {
                chain.next()
            }
            .show()
    }
}
