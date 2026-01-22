# Timing Plugin 故障排查指南

## 常见问题

### 1. ❌ 没有日志输出

#### 症状
运行应用后，Logcat 中没有看到耗时日志。

#### 排查步骤

**Step 1: 检查编译日志**
```bash
./gradlew :demo:assembleDebug --info | grep TimingPlugin
```

期望输出：
```
[TimingPlugin] Found @Timing on com/ccino/demo/MainActivity.onCreate
```

**Step 2: 检查注解 Retention**

❌ 错误（ASM 无法读取）：
```kotlin
@Retention(AnnotationRetention.SOURCE)
```

✅ 正确：
```kotlin
@Retention(AnnotationRetention.BINARY)  // 或 RUNTIME
```

**Step 3: 检查插件是否应用**

在 `demo/build.gradle.kts` 中确认：
```kotlin
plugins {
    id("com.ccino.timing")  // 必须有这一行
}
```

**Step 4: Clean 并重新编译**
```bash
./gradlew clean
./gradlew :demo:assembleDebug
```

### 2. ❌ 编译错误

#### 症状
编译失败，提示找不到插件。

#### 解决方案

**检查 settings.gradle.kts**：
```kotlin
pluginManagement {
    includeBuild("timing-plugin")  // 必须有这一行
    repositories {
        // ...
    }
}
```

### 3. ❌ 插件未检测到注解

#### 症状
编译日志中没有 "Found @Timing"。

#### 可能原因

1. **注解 Retention 错误**
   ```kotlin
   // 必须是 BINARY 或 RUNTIME
   @Retention(AnnotationRetention.BINARY)
   ```

2. **类被过滤**
   检查 `TimingClassVisitorFactory.isInstrumentable()`，确保您的类没有被过滤。

3. **注解路径错误**
   检查 `TimingMethodVisitor.visitAnnotation()` 中的路径：
   ```kotlin
   if (descriptor == "Lcom/ccino/ksp/timing/Timing;")
   ```

### 4. ❌ 运行时崩溃

#### 症状
应用启动后崩溃，提示 VerifyError。

#### 原因
字节码帧计算错误。

#### 解决方案
确保插件中设置了正确的帧计算模式：
```kotlin
variant.instrumentation.setAsmFramesComputationMode(
    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
)
```

### 5. ❌ 日志格式不正确

#### 症状
日志输出但格式错误或缺少信息。

#### 检查
查看 `TimingMethodVisitor.insertTimingLog()` 方法，确保字节码指令正确。

## 调试技巧

### 1. 启用详细日志

编译时添加 `--info` 或 `--debug`：
```bash
./gradlew :demo:assembleDebug --info
```

### 2. 查看生成的字节码

使用 jadx 反编译 APK：
```bash
jadx-gui app/build/outputs/apk/debug/app-debug.apk
```

查看 MainActivity，应该能看到插入的代码：
```java
public void onCreate(Bundle savedInstanceState) {
    long startTime = System.currentTimeMillis();
    try {
        super.onCreate(savedInstanceState);
        // ...
    } finally {
        long duration = System.currentTimeMillis() - startTime;
        Log.d("MainActivity", "onCreate: consume = " + duration + " ms");
    }
}
```

### 3. 添加调试日志

在 `TimingMethodVisitor` 中添加更多日志：
```kotlin
override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
    println("[DEBUG] Visiting annotation: $descriptor on $className.$methodName")
    if (descriptor == "Lcom/ccino/ksp/timing/Timing;") {
        hasTiming = true
        println("[TimingPlugin] Found @Timing on $className.$methodName")
    }
    return super.visitAnnotation(descriptor, visible)
}
```

## 验证清单

在报告问题前，请确认：

- [ ] 注解 Retention 设置为 BINARY 或 RUNTIME
- [ ] 插件已在 demo/build.gradle.kts 中应用
- [ ] settings.gradle.kts 中包含 includeBuild("timing-plugin")
- [ ] 执行了 Clean 并重新编译
- [ ] 编译日志中有 "Found @Timing"
- [ ] Logcat 过滤器设置正确
- [ ] 应用确实执行了带注解的方法

## 成功标志

✅ **编译时**：
```
[TimingPlugin] Applying timing plugin to demo
[TimingPlugin] Found @Timing on com/ccino/demo/MainActivity.onCreate
```

✅ **运行时**：
```
D/MainActivity: onCreate: consume = 123 ms
```

## 需要帮助？

如果以上步骤都无法解决问题，请提供：
1. 完整的编译日志
2. Logcat 输出
3. 注解定义代码
4. build.gradle.kts 配置
