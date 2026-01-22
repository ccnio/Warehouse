# Timing Plugin - ASM 字节码插桩实现

## 简介

使用 **Gradle Transform + ASM 字节码插桩**技术，实现真正的"只加注解就自动统计耗时"功能。

## 特点

✅ **完全透明**：只需在方法上加 `@Timing` 注解，无需修改方法体  
✅ **自动插桩**：编译时自动插入计时代码  
✅ **零运行时开销**：直接修改字节码，无反射  
✅ **异常安全**：使用 try-finally 确保总是记录耗时  

## 使用方式

### 1. 应用插件

在 `demo/build.gradle.kts` 中：

```kotlin
plugins {
    id("com.ccino.timing")
}
```

### 2. 添加注解

```kotlin
@Timing
fun yourMethod() {
    // 方法体 - 无需任何修改！
}
```

### 3. 运行查看日志

```
D/MainActivity: onCreate: consume = 123 ms
```

## 工作原理

### 编译时字节码转换

```
原始代码：
@Timing
fun onCreate() {
    super.onCreate(savedInstanceState)
    // ...
}

↓ 编译时 ASM 插桩

转换后的字节码等价于：
fun onCreate() {
    val startTime = System.currentTimeMillis()
    try {
        super.onCreate(savedInstanceState)
        // ...
    } finally {
        val duration = System.currentTimeMillis() - startTime
        Log.d("MainActivity", "onCreate: consume = $duration ms")
    }
}
```

### 技术栈

- **Gradle Plugin**：注册字节码转换
- **Android Transform API**：拦截编译过程
- **ASM 9.6**：字节码操作框架
- **AdviceAdapter**：方便地在方法前后插入代码

## 实现细节

### 1. 插件注册 (TimingPlugin.kt)

```kotlin
class TimingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions
            .findByType(AndroidComponentsExtension::class.java)
        
        androidComponents?.onVariants { variant ->
            variant.instrumentation.transformClassesWith(
                TimingClassVisitorFactory::class.java,
                InstrumentationScope.ALL
            ) { }
        }
    }
}
```

### 2. 类访问器 (TimingClassVisitor.kt)

```kotlin
class TimingClassVisitor(nextClassVisitor: ClassVisitor) 
    : ClassVisitor(ASM9, nextClassVisitor) {
    
    override fun visitMethod(...): MethodVisitor {
        val mv = super.visitMethod(...)
        return TimingMethodVisitor(mv, access, name, descriptor, className)
    }
}
```

### 3. 方法访问器 (TimingMethodVisitor.kt)

使用 `AdviceAdapter` 在方法入口和出口插入代码：

```kotlin
class TimingMethodVisitor(...) : AdviceAdapter(...) {
    
    override fun onMethodEnter() {
        // 插入：val startTime = System.currentTimeMillis()
    }
    
    override fun onMethodExit(opcode: Int) {
        // 插入：计算耗时并打印日志
    }
}
```

## 优势对比

| 方案 | 使用方式 | 性能 | 维护性 |
|------|---------|------|--------|
| **ASM 插桩（本方案）** | 只加注解 ✅ | 最优 ✅ | 中等 |
| KSP + 内联函数 | 需要包装方法体 | 优秀 | 最佳 ✅ |
| AspectJ | 只加注解 ✅ | 良好 | 中等 |
| Kotlin 编译器插件 | 只加注解 ✅ | 最优 ✅ | 差 ❌ |

## 注意事项

1. **编译时间**：首次编译会稍慢（需要处理字节码）
2. **调试**：字节码被修改，调试时可能看到插入的代码
3. **混淆**：需要保留 `@Timing` 注解

## ProGuard 配置

如果使用混淆，需要保留注解：

```proguard
-keepattributes *Annotation*
-keep @interface com.ccino.ksp.timing.Timing
```

## 故障排查

### 插件未生效

检查：
1. `settings.gradle.kts` 是否包含 `include(":timing-plugin")`
2. `build.gradle.kts` 是否添加了 `classpath(project(":timing-plugin"))`
3. `demo/build.gradle.kts` 是否应用了插件

### 编译错误

确保：
- Gradle 版本 >= 8.0
- AGP 版本 >= 8.0
- ASM 版本 = 9.6

## 扩展

可以扩展插件支持：
- 自定义日志 TAG
- 配置最小耗时阈值
- 支持方法参数记录
- 生成性能报告
