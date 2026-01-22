# @Timing 注解使用指南

## ✅ 问题已解决

**关键修复**：将 `@Retention` 从 `SOURCE` 改为 `BINARY`

```kotlin
// 修改前（ASM 无法读取）
@Retention(AnnotationRetention.SOURCE)

// 修改后（ASM 可以读取）
@Retention(AnnotationRetention.BINARY)
```

## 🎯 使用方式

### 1. 在方法上添加注解

```kotlin
class MainActivity : ComponentActivity() {
    
    @Timing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 您的代码 - 无需任何修改！
    }
}
```

### 2. 编译项目

```bash
./gradlew :demo:assembleDebug
```

### 3. 查看编译日志

编译时会看到插件工作的日志：

```
[TimingPlugin] Found @Timing on com/ccino/demo/MainActivity.onCreate
```

### 4. 运行应用查看 Logcat

```
D/MainActivity: onCreate: consume = 123 ms
```

## 📊 验证插件是否生效

### 方法 1：查看编译日志

```bash
./gradlew :demo:assembleDebug --info | grep TimingPlugin
```

应该看到：
```
[TimingPlugin] Applying timing plugin to demo
[TimingPlugin] Configuring variant: debug
[TimingPlugin] Found @Timing on com/ccino/demo/MainActivity.onCreate
```

### 方法 2：反编译 APK

使用 jadx 或 Android Studio 的 APK Analyzer 查看反编译后的代码，应该能看到插入的计时代码。

### 方法 3：运行应用

在 Logcat 中过滤 `MainActivity`，应该能看到耗时日志。

## 🔍 故障排查

### 问题 1：编译日志中没有 "Found @Timing"

**原因**：注解的 Retention 设置错误

**解决**：确保注解定义为：
```kotlin
@Retention(AnnotationRetention.BINARY)  // 或 RUNTIME
```

### 问题 2：运行时没有日志输出

**检查清单**：
1. ✅ 编译日志中有 "Found @Timing"
2. ✅ Logcat 过滤器设置正确
3. ✅ 日志级别设置为 Debug 或更低
4. ✅ 应用确实执行了该方法

### 问题 3：插件未应用

**检查**：
```kotlin
// demo/build.gradle.kts
plugins {
    id("com.ccino.timing")  // 确保有这一行
}
```

## 📝 测试用例

项目中包含完整的测试示例：

1. **MainActivity.kt** - 实际使用场景
2. **TimingTestActivity.kt** - 各种测试场景
   - 普通方法
   - 有返回值的方法
   - 带参数的方法
   - 异常情况

## 🎉 成功标志

当您看到以下输出时，说明插件工作正常：

**编译时：**
```
[TimingPlugin] Found @Timing on com/ccino/demo/MainActivity.onCreate
```

**运行时：**
```
D/MainActivity: onCreate: consume = 123 ms
```

## 💡 提示

1. **首次编译**：可能需要 Clean 项目
   ```bash
   ./gradlew clean
   ```

2. **增量编译**：修改代码后只会处理变更的类

3. **性能影响**：编译时间会略微增加（需要处理字节码）

4. **调试**：字节码被修改，断点调试时会看到插入的代码

## 📚 更多信息

详细技术文档请查看：`timing-plugin/README.md`
