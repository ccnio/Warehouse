# Timing 模块架构说明

## 📁 项目结构

```
Warehouse/                          # 根项目
│
├── timing-annotation/              # 模块 1：注解模块（子模块）
│   ├── build.gradle.kts
│   ├── README.md
│   └── src/main/kotlin/
│       └── com/ccino/timing/annotation/
│           └── Timing.kt           # @Timing 注解定义
│
├── timing-plugin/                  # 模块 2：插件模块（复合构建）
│   ├── build.gradle.kts
│   ├── settings.gradle.kts         # 独立的 settings
│   ├── README.md
│   └── src/main/kotlin/
│       └── com/ccino/timing/plugin/
│           ├── TimingPlugin.kt     # Gradle 插件入口
│           ├── TimingExtension.kt  # 配置扩展
│           ├── TimingClassVisitorFactory.kt
│           └── TimingMethodVisitor.kt  # ASM 字节码处理
│
└── demo/                           # 示例应用模块
    ├── build.gradle.kts
    └── src/.../MainActivity.kt
```

## 🎯 两个独立 Module 的设计

### 1️⃣ timing-annotation（普通子模块）

**类型**: 标准 Kotlin JVM 模块  
**位置**: `Warehouse/timing-annotation/`  
**引入方式**: `include(":timing-annotation")` in `settings.gradle.kts`

**特点**:
- ✅ 纯注解定义，无任何依赖
- ✅ 体积极小（< 5KB）
- ✅ 可被应用模块直接依赖
- ✅ 可以独立发布到 Maven

**构建配置**:
```kotlin
// timing-annotation/build.gradle.kts
plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
```

**构建命令**:
```bash
./gradlew :timing-annotation:build
```

---

### 2️⃣ timing-plugin（复合构建模块）

**类型**: Gradle Plugin 模块（includeBuild）  
**位置**: `Warehouse/timing-plugin/`  
**引入方式**: `includeBuild("timing-plugin")` in `pluginManagement`

**特点**:
- ✅ 包含 ASM 字节码处理逻辑
- ✅ 编译时使用，不打包到 APK
- ✅ 完全独立，有自己的 `settings.gradle.kts`
- ✅ 与注解模块完全解耦（通过字符串路径识别注解）
- ✅ 可以独立发布到 Gradle Plugin Portal

**构建配置**:
```kotlin
// timing-plugin/build.gradle.kts
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    // 注意：不依赖 timing-annotation
}
```

**构建命令**:
```bash
./gradlew :timing-plugin:build
```

---

## 🔗 模块关系

```
┌─────────────────────┐
│  timing-annotation  │  ← 纯注解，无依赖
└─────────────────────┘
          ↑
          │ (运行时依赖)
          │
    ┌─────────┐
    │  demo   │
    └─────────┘
          ↑
          │ (编译时使用插件)
          │
┌─────────────────────┐
│   timing-plugin     │  ← 通过配置的字符串路径识别注解
└─────────────────────┘
```

### 关键点：插件与注解的解耦

插件**不直接依赖**注解模块，而是通过配置的注解路径来识别：

```kotlin
// demo/build.gradle.kts
timing {
    annotationClass = "com.ccino.timing.annotation.Timing"  // 字符串路径
}
```

插件在字节码中检测注解：
```kotlin
// TimingMethodVisitor.kt
override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
    // descriptor = "Lcom/ccino/timing/annotation/Timing;"
    if (descriptor == "L${annotationPath.replace('.', '/')};") {
        hasTiming = true
    }
    return super.visitAnnotation(descriptor, visible)
}
```

## 🚀 使用方式

### 1. 添加注解依赖
```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":timing-annotation"))
}
```

### 2. 应用插件
```kotlin
// app/build.gradle.kts
plugins {
    id("com.ccino.timing")
}

timing {
    annotationClass = "com.ccino.timing.annotation.Timing"
    logTag = "Performance"
    minDuration = 10
}
```

### 3. 使用注解
```kotlin
import com.ccino.timing.annotation.Timing

class MainActivity : AppCompatActivity() {
    @Timing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 自动计时
    }
}
```

## 📦 发布配置

### 发布注解模块到 Maven
```kotlin
// timing-annotation/build.gradle.kts
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.ccino"
            artifactId = "timing-annotation"
            version = "1.0.0"
            from(components["java"])
        }
    }
}
```

### 发布插件到 Gradle Plugin Portal
```kotlin
// timing-plugin/build.gradle.kts
gradlePlugin {
    plugins {
        create("timingPlugin") {
            id = "com.ccino.timing"
            implementationClass = "com.ccino.timing.plugin.TimingPlugin"
            displayName = "Timing Plugin"
            description = "Automatic method timing using ASM"
        }
    }
}
```

## ✨ 架构优势

| 特性 | 说明 |
|------|------|
| **职责分离** | 注解定义契约，插件提供实现 |
| **完全解耦** | 插件通过字符串路径识别注解，无编译依赖 |
| **独立构建** | 两个模块可以独立编译和发布 |
| **版本独立** | 注解和插件可以独立升级版本 |
| **体积优化** | 应用只依赖轻量的注解，插件不打包到 APK |
| **易于测试** | 两个模块可以独立测试 |
| **可复用** | 注解可以用于其他平台（JVM、Kotlin Multiplatform） |

## 🎓 业界对比

| 库 | 注解模块 | 处理器模块 | 架构 |
|---|---------|-----------|------|
| **Timing** | timing-annotation | timing-plugin | ✅ 分离 |
| **Dagger** | dagger | dagger-compiler | ✅ 分离 |
| **Room** | room-runtime | room-compiler | ✅ 分离 |
| **Glide** | glide | glide-compiler | ✅ 分离 |

## 📝 总结

当前架构完全符合业界最佳实践：
- ✅ **timing-annotation**: 轻量级注解模块，作为根项目的子模块
- ✅ **timing-plugin**: 重量级插件模块，作为独立的复合构建
- ✅ **完全解耦**: 插件不依赖注解模块，通过配置关联
- ✅ **易于发布**: 两个模块可以独立发布到 Maven/Gradle Plugin Portal
