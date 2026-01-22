package com.ccino.timing.plugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationParameters
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property

/**
 * Timing 插件 - 使用 ASM 字节码插桩实现方法耗时统计
 * 
 * 使用方式：
 * 1. 在 build.gradle 中应用插件并配置：
 *    plugins {
 *        id("com.ccino.timing")
 *    }
 *    
 *    timing {
 *        enabled = true
 *        annotationClass = "com.ccino.ksp.timing.Timing"  // 自定义注解类
 *        logTag = "Performance"                           // 自定义日志 TAG
 *        minDuration = 10                                 // 只记录超过 10ms 的
 *    }
 * 
 * 2. 在方法上添加 @Timing 注解：
 *    @Timing
 *    fun yourMethod() {
 *        // 方法体
 *    }
 * 
 * 3. 插件会自动在编译时插入计时代码，无需修改方法体
 */
class TimingPlugin : Plugin<Project> {
    
    override fun apply(project: Project) {
        println("[TimingPlugin] Applying timing plugin to ${project.name}")
        
        // 创建配置扩展
        val extension = project.extensions.create("timing", TimingExtension::class.java)
        
        // 获取 Android 扩展
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        
        androidComponents?.onVariants { variant ->
            println("[TimingPlugin] Configuring variant: ${variant.name}")
            
            // 注册 ASM 字节码转换
            variant.instrumentation.transformClassesWith(
                TimingClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { params ->
                // 传递配置参数到 ClassVisitor
                params.annotationDescriptor.set(extension.getAnnotationDescriptor())
                params.logTag.set(extension.logTag)
                params.minDuration.set(extension.minDuration)
                params.enabled.set(extension.enabled)
            }
            
            // 设置帧计算模式
            variant.instrumentation.setAsmFramesComputationMode(
                FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}

/**
 * 插件参数接口
 */
interface TimingParams : InstrumentationParameters {
    @get:org.gradle.api.tasks.Input
    val annotationDescriptor: Property<String>
    
    @get:org.gradle.api.tasks.Input
    @get:org.gradle.api.tasks.Optional
    val logTag: Property<String?>
    
    @get:org.gradle.api.tasks.Input
    val minDuration: Property<Long>
    
    @get:org.gradle.api.tasks.Input
    val enabled: Property<Boolean>
}
