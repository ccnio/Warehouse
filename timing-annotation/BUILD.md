# timing-annotation 模块构建说明

## 📦 模块信息

- **模块名称**: timing-annotation
- **类型**: 标准 Kotlin JVM 模块
- **位置**: `Warehouse/timing-annotation/`
- **功能**: 提供 `@Timing` 注解定义

## 🔨 构建命令

### 编译模块
```bash
./gradlew :timing-annotation:build
```

### 清理构建产物
```bash
./gradlew :timing-annotation:clean
```

### 生成 JAR
```bash
./gradlew :timing-annotation:jar
```

输出位置: `timing-annotation/build/libs/timing-annotation.jar`

## 📋 模块依赖

此模块**无任何外部依赖**，只依赖 Kotlin 标准库。

## 🚀 发布到本地 Maven

```bash
./gradlew :timing-annotation:publishToMavenLocal
```

发布后可以在其他项目中使用：
```kotlin
dependencies {
    implementation("com.ccino:timing-annotation:1.0.0")
}
```

## 📝 版本管理

当前版本定义在 `build.gradle.kts` 中：
```kotlin
group = "com.ccino"
version = "1.0.0"
```

## ✅ 模块独立性

此模块完全独立，可以：
- ✅ 单独编译
- ✅ 单独测试
- ✅ 单独发布
- ✅ 被任意项目依赖
