package com.ccino.timing.plugin

/**
 * Timing 插件配置扩展
 * 
 * 使用示例：
 * timing {
 *     enabled = true
 *     annotationClass = "com.your.package.Timing"
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
     * 自定义注解类的全限定名
     * 默认：com.ccino.ksp.timing.Timing
     */
    var annotationClass: String = "com.ccino.ksp.timing.Timing"
    
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
     * 是否记录方法参数
     */
    var logParameters: Boolean = false
    
    /**
     * 排除的包名列表
     */
    var excludePackages: List<String> = listOf(
        "android.",
        "androidx.",
        "kotlin.",
        "java.",
        "javax."
    )
    
    /**
     * 获取注解的字节码描述符
     * 例如：com.ccino.ksp.timing.Timing -> Lcom/ccino/ksp/timing/Timing;
     */
    fun getAnnotationDescriptor(): String {
        return "L${annotationClass.replace('.', '/')};"
    }
    
    /**
     * 检查包名是否应该被排除
     */
    fun shouldExcludePackage(packageName: String): Boolean {
        return excludePackages.any { packageName.startsWith(it) }
    }
}
