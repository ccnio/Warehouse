# Timing Plugin 配置指南

## 问题：注解包名变化导致插件失效

### ❌ 问题场景

如果您的 `@Timing` 注解包名从：
```kotlin
package com.ccino.ksp.timing
```

变更为：
```kotlin
package com.your.custom.package
```

插件会失效，因为硬编码的注解路径不匹配。

### ✅ 解决方案：可配置的插件

现在插件支持**完全可配置**，可以指定任意注解类！

## 配置方式

在 `demo/build.gradle.kts` 中：

```kotlin
plugins {
    id("com.ccino.timing")
}

timing {
    // 是否启用插件
    enabled = true
    
    // 自定义注解类的全限定名（支持任意包名）
    annotationClass = "com.ccino.ksp.timing.Timing"
    
    // 自定义日志 TAG（null 表示使用类名）
    logTag = "Performance"
    
    // 最小耗时阈值（毫秒），只记录超过此值的方法
    minDuration = 10
}
```

## 配置选项详解

### 1. `enabled`

**类型**：`Boolean`  
**默认值**：`true`  
**说明**：是否启用插件

```kotlin
timing {
    enabled = true  // 启用
    // enabled = false  // 禁用（不处理任何类）
}
```

### 2. `annotationClass`

**类型**：`String`  
**默认值**：`"com.ccino.ksp.timing.Timing"`  
**说明**：指定要识别的注解类全限定名

```kotlin
timing {
    // 默认注解
    annotationClass = "com.ccino.ksp.timing.Timing"
    
    // 自定义注解
    annotationClass = "com.your.package.PerformanceTrace"
    
    // 其他框架的注解
    annotationClass = "androidx.tracing.Trace"
}
```

**使用场景**：
- 注解包名变更
- 使用自定义注解
- 兼容其他框架的注解

### 3. `logTag`

**类型**：`String?`  
**默认值**：`null`（使用类名）  
**说明**：自定义日志 TAG

```kotlin
timing {
    logTag = null  // 使用类名，如 "MainActivity"
    logTag = "Performance"  // 统一使用 "Performance"
    logTag = "Timing"  // 统一使用 "Timing"
}
```

**效果对比**：

```kotlin
// logTag = null
D/MainActivity: onCreate: consume = 123 ms
D/UserService: login: consume = 456 ms

// logTag = "Performance"
D/Performance: onCreate: consume = 123 ms
D/Performance: login: consume = 456 ms
```

### 4. `minDuration`

**类型**：`Long`  
**默认值**：`0`（记录所有）  
**说明**：最小耗时阈值（毫秒），只记录超过此值的方法

```kotlin
timing {
    minDuration = 0   // 记录所有方法
    minDuration = 10  // 只记录超过 10ms 的方法
    minDuration = 100 // 只记录超过 100ms 的方法
}
```

**使用场景**：
- 过滤掉执行很快的方法
- 只关注性能瓶颈
- 减少日志输出

## 使用示例

### 示例 1：默认配置

```kotlin
plugins {
    id("com.ccino.timing")
}

// 不配置，使用默认值
```

### 示例 2：自定义注解

```kotlin
// 1. 定义自己的注解
package com.myapp.performance

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Trace

// 2. 配置插件
timing {
    annotationClass = "com.myapp.performance.Trace"
}

// 3. 使用注解
@Trace
fun myMethod() {
    // ...
}
```

### 示例 3：统一日志 TAG

```kotlin
timing {
    logTag = "Performance"
    minDuration = 50  // 只记录超过 50ms 的
}
```

输出：
```
D/Performance: onCreate: consume = 123 ms
D/Performance: loadData: consume = 89 ms
// 低于 50ms 的方法不会输出
```

### 示例 4：开发/生产环境切换

```kotlin
timing {
    enabled = project.hasProperty("enableTiming")
    minDuration = if (project.hasProperty("release")) 100 else 0
}
```

使用：
```bash
# 开发环境：记录所有
./gradlew assembleDebug

# 生产环境：只记录超过 100ms 的
./gradlew assembleRelease -Prelease
```

## 多注解支持

### 场景：同时支持多个注解

如果需要同时支持多个注解（如 `@Timing` 和 `@Trace`），可以：

**方案 1：创建统一注解**
```kotlin
// 创建一个统一的注解
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Performance

// 配置
timing {
    annotationClass = "com.myapp.Performance"
}
```

**方案 2：扩展插件支持多注解**（需要修改插件代码）
```kotlin
timing {
    annotationClasses = listOf(
        "com.ccino.ksp.timing.Timing",
        "com.myapp.Trace"
    )
}
```

## 注意事项

### 1. 注解 Retention 必须正确

```kotlin
// ❌ 错误：SOURCE 会被编译器丢弃
@Retention(AnnotationRetention.SOURCE)

// ✅ 正确：BINARY 或 RUNTIME
@Retention(AnnotationRetention.BINARY)
@Retention(AnnotationRetention.RUNTIME)
```

### 2. 全限定名必须完整

```kotlin
// ❌ 错误：缺少包名
annotationClass = "Timing"

// ✅ 正确：完整的包名
annotationClass = "com.ccino.ksp.timing.Timing"
```

### 3. 配置变更后需要 Clean

```bash
./gradlew clean
./gradlew assembleDebug
```

## 验证配置

### 1. 查看编译日志

```bash
./gradlew assembleDebug --info | grep TimingPlugin
```

应该看到：
```
[TimingPlugin] Found annotation Lcom/ccino/ksp/timing/Timing; on com/ccino/demo/MainActivity.onCreate
```

### 2. 检查注解描述符

注解类 → 字节码描述符转换规则：
```
com.ccino.ksp.timing.Timing
↓
Lcom/ccino/ksp/timing/Timing;
```

## 总结

通过配置化设计，插件现在可以：

✅ **支持任意包名的注解**  
✅ **自定义日志 TAG**  
✅ **过滤快速方法**  
✅ **灵活启用/禁用**  

**不再需要担心注解包名变化导致插件失效！**
