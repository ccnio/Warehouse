# 插件与注解关联机制

## 问题：包名变化导致插件失效

### 原始问题

**硬编码的注解路径**：
```kotlin
// TimingMethodVisitor.kt
if (descriptor == "Lcom/ccino/ksp/timing/Timing;") {
    hasTiming = true
}
```

**问题**：
- ❌ 注解包名变化，插件立即失效
- ❌ 无法使用自定义注解
- ❌ 无法兼容其他框架

## 解决方案：配置化设计

### 1. 创建配置扩展类

```kotlin
// TimingExtension.kt
open class TimingExtension {
    var annotationClass: String = "com.ccino.ksp.timing.Timing"
    
    fun getAnnotationDescriptor(): String {
        return "L${annotationClass.replace('.', '/')};"
    }
}
```

### 2. 在插件中注册配置

```kotlin
// TimingPlugin.kt
class TimingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 创建配置扩展
        val extension = project.extensions.create("timing", TimingExtension::class.java)
        
        // 传递配置到 ClassVisitor
        variant.instrumentation.transformClassesWith(...) { params ->
            params.annotationDescriptor.set(extension.getAnnotationDescriptor())
        }
    }
}
```

### 3. 在 MethodVisitor 中使用配置

```kotlin
// TimingMethodVisitor.kt
class TimingMethodVisitor(
    ...,
    private val annotationDescriptor: String  // 从配置传入
) : AdviceAdapter(...) {
    
    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        // 使用配置的注解描述符
        if (descriptor == annotationDescriptor) {
            hasTiming = true
        }
        return super.visitAnnotation(descriptor, visible)
    }
}
```

## 使用方式

### 场景 1：注解包名变化

```kotlin
// 原来的注解
package com.ccino.ksp.timing
annotation class Timing

// 新的注解位置
package com.myapp.performance
annotation class Timing

// 配置插件
timing {
    annotationClass = "com.myapp.performance.Timing"
}
```

### 场景 2：使用自定义注解

```kotlin
// 定义自己的注解
package com.mycompany.trace

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class PerformanceTrace

// 配置插件
timing {
    annotationClass = "com.mycompany.trace.PerformanceTrace"
}

// 使用
@PerformanceTrace
fun myMethod() { }
```

### 场景 3：兼容其他框架

```kotlin
// 使用 AndroidX Tracing 注解
timing {
    annotationClass = "androidx.tracing.Trace"
}

// 使用
@androidx.tracing.Trace
fun myMethod() { }
```

## 技术细节

### 注解描述符转换

Java/Kotlin 类名 → 字节码描述符：

```
类名格式：
com.ccino.ksp.timing.Timing

↓ 转换规则：
1. 替换 . 为 /
2. 前面加 L
3. 后面加 ;

字节码描述符：
Lcom/ccino/ksp/timing/Timing;
```

**实现代码**：
```kotlin
fun getAnnotationDescriptor(): String {
    return "L${annotationClass.replace('.', '/')};"
}
```

### 参数传递流程

```
build.gradle.kts (配置)
    ↓
TimingExtension (扩展类)
    ↓
TimingPlugin (插件)
    ↓
TimingParams (参数接口)
    ↓
TimingClassVisitorFactory (工厂)
    ↓
TimingClassVisitor (类访问器)
    ↓
TimingMethodVisitor (方法访问器)
    ↓
visitAnnotation (检查注解)
```

### 完整的参数接口

```kotlin
interface TimingParams : InstrumentationParameters {
    val annotationDescriptor: Property<String>  // 注解描述符
    val logTag: Property<String?>               // 日志 TAG
    val minDuration: Property<Long>             // 最小耗时
    val enabled: Property<Boolean>              // 是否启用
}
```

## 扩展功能

### 1. 多注解支持（未来扩展）

```kotlin
timing {
    annotationClasses = listOf(
        "com.ccino.ksp.timing.Timing",
        "com.myapp.Trace",
        "androidx.tracing.Trace"
    )
}
```

### 2. 条件配置

```kotlin
timing {
    annotationClass = if (isDebug) {
        "com.myapp.DebugTiming"
    } else {
        "com.myapp.ReleaseTiming"
    }
}
```

### 3. 动态加载配置

```kotlin
timing {
    val config = file("timing-config.properties")
    annotationClass = config.readText().trim()
}
```

## 优势总结

| 特性 | 硬编码方案 | 配置化方案 |
|------|-----------|-----------|
| **包名变化** | ❌ 失效 | ✅ 配置即可 |
| **自定义注解** | ❌ 不支持 | ✅ 完全支持 |
| **框架兼容** | ❌ 不支持 | ✅ 完全支持 |
| **灵活性** | ❌ 低 | ✅ 高 |
| **维护性** | ❌ 差 | ✅ 好 |

## 最佳实践

### 1. 注解定义规范

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)  // 必须是 BINARY 或 RUNTIME
annotation class YourAnnotation
```

### 2. 配置规范

```kotlin
timing {
    // 使用完整的包名
    annotationClass = "com.your.package.YourAnnotation"
    
    // 不要使用简单类名
    // annotationClass = "YourAnnotation"  // ❌ 错误
}
```

### 3. 验证配置

```bash
# 编译并查看日志
./gradlew assembleDebug --info | grep "Found annotation"

# 应该看到正确的注解描述符
[TimingPlugin] Found annotation Lcom/your/package/YourAnnotation; on ...
```

## 总结

通过**配置化设计**，插件与注解的关联变得：

✅ **灵活**：支持任意包名  
✅ **可扩展**：支持自定义注解  
✅ **可维护**：配置变更无需修改代码  
✅ **可复用**：可用于不同项目  

**核心思想**：将硬编码的依赖转换为可配置的参数，通过 Gradle 扩展传递到字节码处理器。
