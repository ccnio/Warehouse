package com.ccino.timing.plugin

/**
 * Timing 插件配置扩展
 * 
 * 使用示例：
 * timing {
 *     enabled = true
 *     // annotationClass 自动使用默认值 com.ccino.timing.annotation.Timing
 *     logTag = "Performance"
 *     minDuration = 10 // 只记录超过 10ms 的方法
 * }
 */
open class TimingExtension {
    /**
     * 是否启用插件
     */
    var enabled: Boolean = true
    
    /**
     * 注解类的全限定名
     * 
     * 默认：com.ccino.timing.annotation.Timing
     * 如果你的注解在其他包，可以修改此值
     * 
     * 示例：annotationClass = "com.mycompany.MyTiming"
     */
    var annotationClass: String = "com.ccino.timing.annotation.Timing"  // ✅ 合理的默认值
    
    /**
     * 自定义日志 TAG
     * 如果为 null，使用类名
     */
    var logTag: String? = null
    
    /**
     * 最小耗时阈值（毫秒）
     * 只记录耗时超过此值的方法
     * 0 表示记录所有
     */
    var minDuration: Long = 0
    
    /**
     * 是否处理依赖库（jar/aar）
     * 
     * - false（推荐）：只处理项目代码，编译快 ⚡
     * - true：处理项目 + 所有依赖，编译慢但功能完整 🐌
     * 
     * 默认：false（性能优先）
     */
    var instrumentDependencies: Boolean = false
    
    /**
     * 只处理这些包名（白名单）
     * 为空则处理所有（根据 excludePackages 过滤）
     * 
     * 示例：listOf("com.ccino.", "com.mycompany.")
     */
    var includePackages: List<String> = emptyList()
    
    /**
     * 排除的包名列表（黑名单）
     */
    var excludePackages: List<String> = listOf(
        "android.",
        "androidx.",
        "kotlin.",
        "kotlinx.",
        "java.",
        "javax.",
        "com.google.",
        "com.squareup.",
        "okhttp3.",
        "retrofit2.",
        "com.bumptech.glide."
    )
    
    /**
     * 获取注解的字节码描述符
     * 例如：com.ccino.ksp.timing.Timing -> Lcom/ccino/ksp/timing/Timing;
     */
    fun getAnnotationDescriptor(): String {
        return "L${annotationClass.replace('.', '/')};"
    }
    
    /**
     * 检查类是否应该被处理
     */
    fun shouldInstrument(className: String): Boolean {
        // 1. 如果有白名单，只处理白名单中的类
        if (includePackages.isNotEmpty()) {
            return includePackages.any { className.startsWith(it) }
        }
        
        // 2. 否则，排除黑名单中的类
        return !excludePackages.any { className.startsWith(it) }
    }
}
