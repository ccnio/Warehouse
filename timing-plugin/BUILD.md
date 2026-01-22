# timing-plugin 模块构建说明

## 📦 模块信息

- **模块名称**: timing-plugin
- **类型**: Gradle Plugin 模块（includeBuild）
- **位置**: `Warehouse/timing-plugin/`
- **功能**: 提供 ASM 字节码插桩实现

## 🔨 构建命令

### 从根项目编译
```bash
cd /Users/dxm/code/Warehouse
./gradlew :timing-plugin:build
```

### 查看可用任务
```bash
./gradlew :timing-plugin:tasks
```

### 生成插件 JAR
```bash
./gradlew :timing-plugin:jar
```

输出位置: `timing-plugin/build/libs/timing-plugin.jar`

## 📋 模块依赖

```kotlin
dependencies {
    // Android Gradle Plugin
    implementation("com.android.tools.build:gradle:8.2.0")
    
    // ASM 字节码处理库
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
}
```

**注意**: 此模块**不依赖** `timing-annotation`，通过配置的字符串路径识别注解。

## 🚀 插件 ID

```kotlin
id = "com.ccino.timing"
```

应用模块使用：
```kotlin
plugins {
    id("com.ccino.timing")
}
```

## 🔗 与根项目的关系

此模块通过 `includeBuild` 引入到根项目：

```kotlin
// Warehouse/settings.gradle.kts
pluginManagement {
    includeBuild("timing-plugin")
}
```

这意味着：
- ✅ 插件有自己的 `settings.gradle.kts`
- ✅ 可以独立构建和测试
- ✅ 自动注册到根项目的 pluginManagement
- ✅ demo 模块可以直接使用 `id("com.ccino.timing")`

## 📝 发布到本地

```bash
./gradlew :timing-plugin:publishToMavenLocal
```

## 🧪 测试插件

在 demo 模块中测试：
```bash
./gradlew :demo:assembleDebug --info | grep TimingPlugin
```

查看插件日志：
```
[TimingPlugin] Applying timing plugin to demo
[TimingPlugin] Configuring variant: debug
```

## ✅ 模块独立性

此模块完全独立，可以：
- ✅ 单独编译
- ✅ 单独测试
- ✅ 单独发布
- ✅ 移植到其他项目
- ✅ 发布到 Gradle Plugin Portal

## 🎯 解耦设计

插件与注解模块完全解耦：
- ❌ 不依赖 `timing-annotation` 模块
- ✅ 通过配置的注解路径（字符串）识别注解
- ✅ 可以支持任意包名的 `@Timing` 注解

示例配置：
```kotlin
timing {
    annotationClass = "com.ccino.timing.annotation.Timing"  // 可配置
}
```
