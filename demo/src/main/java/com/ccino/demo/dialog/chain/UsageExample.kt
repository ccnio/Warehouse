package com.ccino.demo.dialog.chain

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

/**
 * DialogChainController 使用示例
 * 
 * 优化后的特性：
 * 1. ✅ 任务去重 - 相同ID的任务不会重复添加
 * 2. ✅ 超时保护 - 异步prepare超过30秒自动跳过
 * 3. ✅ 状态管理 - IDLE/PREPARING/SHOWING 三态管理
 * 4. ✅ 完整日志 - 方便调试和问题排查
 * 5. ✅ 单个移除 - 可以移除指定ID的任务
 */

/**
 * 在 Activity 中使用
 */
class ExampleActivity : ComponentActivity() {
    
    private val dialogController = DialogChainController()
    
    override fun onStart() {
        super.onStart()
        
        // 绑定生命周期
        dialogController.attach(this)
        
        // 添加多个弹窗任务（按优先级排序）
        dialogController
            .addTask(NoticeTask())      // 优先级=100（最高）
            .addTask(AdTask())           // 优先级=50
            .addTask(UpdateTask())       // 优先级=30
            .start()
        
        // 查看状态（调试用）
        println(dialogController.getStatus())
    }
    
    /**
     * 演示任务去重
     */
    fun demonstrateDeduplication() {
        dialogController
            .addTask(AdTask())   // 第一次添加
            .addTask(AdTask())   // 第二次添加，会被忽略（相同id）
            .start()
        
        // 队列中只有1个 AdTask
    }
    
    /**
     * 演示移除任务
     */
    fun demonstrateRemoval() {
        dialogController
            .addTask(NoticeTask())
            .addTask(AdTask())
            .removeTask("ad_task")  // 移除广告任务
            .start()
        
        // 只会显示 NoticeTask
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Controller 会自动在 onDestroy 时清理
        // 也可以手动解绑: dialogController.detach()
    }
}

/**
 * 在 Fragment 中使用（推荐）
 */
class ExampleFragment : Fragment() {
    
    private val dialogController = DialogChainController()
    
    override fun onStart() {
        super.onStart()
        
        // 自动使用 viewLifecycleOwner
        dialogController.attach(this)
        
        // 添加任务
        dialogController
            .addTask(NoticeTask())
            .addTask(UpdateTask())
            .start()
    }
}

/**
 * 自定义任务示例
 */
class CustomTask : DialogTask(
    priority = 80,
    id = "custom_task"  // 唯一标识
) {
    
    // 1. 异步准备阶段（可选）
    override fun prepare(
        host: androidx.lifecycle.LifecycleOwner,
        activity: ComponentActivity,
        callback: (Boolean) -> Unit
    ) {
        // 模拟异步检查（如网络请求）
        checkShouldShowAsync { shouldShow ->
            callback(shouldShow)
        }
    }
    
    // 2. 显示弹窗
    override fun onShow(
        host: androidx.lifecycle.LifecycleOwner,
        activity: ComponentActivity,
        chain: ChainCallback
    ) {
        // 显示你的弹窗
        showMyDialog(activity) {
            // 弹窗关闭后
            chain.next()  // 继续下一个
            // 或
            // chain.stop()  // 中断后续所有任务
        }
    }
    
    private fun checkShouldShowAsync(callback: (Boolean) -> Unit) {
        // 实现异步检查逻辑
        callback(true)
    }
    
    private fun showMyDialog(activity: ComponentActivity, onDismiss: () -> Unit) {
        // 实现弹窗显示逻辑
        onDismiss()
    }
}

/**
 * 使用技巧
 */
object DialogChainTips {
    
    /**
     * 优先级建议：
     * - 100+  : 紧急通知、重要提示
     * - 50-99 : 广告、运营活动
     * - 10-49 : 更新提示、功能引导
     * - 1-9   : 普通提示
     */
    
    /**
     * ID命名建议：
     * - 使用有意义的名称：ad_task, notice_task
     * - 可以包含日期：ad_2024_01_21（每日只显示一次）
     * - 空字符串表示不去重
     */
    
    /**
     * 最佳实践：
     * 1. 在 Fragment 中使用（自动使用 viewLifecycleOwner）
     * 2. 为每个任务设置唯一ID（避免重复）
     * 3. 异步prepare不要超过30秒
     * 4. 在 onDismiss 中调用 chain.next() 或 chain.stop()
     * 5. 使用日志查看执行状态：dialogController.getStatus()
     */
}
