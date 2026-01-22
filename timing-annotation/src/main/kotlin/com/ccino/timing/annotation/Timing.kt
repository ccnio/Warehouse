package com.ccino.timing.annotation

/**
 * 用于标记需要统计耗时的方法
 * 
 * 使用方式：
 * 在方法上添加此注解后，timing-plugin 会在编译时自动插入耗时统计代码
 * 
 * 示例：
 * ```kotlin
 * @Timing
 * override fun onCreate(savedInstanceState: Bundle?) {
 *     super.onCreate(savedInstanceState)
 *     // 方法逻辑
 * }
 * ```
 * 
 * 注意：
 * - Retention 必须是 BINARY 或 RUNTIME，才能被 ASM 读取
 * - 插件会在方法开始时记录时间，在方法结束前打印日志
 * - 日志格式：Log.d(类名, "方法名: consume = xxx ms")
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Timing
