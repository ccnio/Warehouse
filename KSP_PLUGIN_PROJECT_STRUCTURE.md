# KSP + Plugin 组合项目结构 - 业界标准

## 📁 推荐的目录结构

### 方案 A：单仓库多模块（推荐）⭐⭐⭐⭐⭐

```
project-root/
│
├── settings.gradle.kts              # 根项目配置
├── build.gradle.kts
│
├── annotation/                      # 模块 1：注解定义
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── com/company/annotation/
│           ├── MyAnnotation.kt      # 注解
│           └── Generated.kt         # 生成代码标记注解（可选）
│
├── processor/                       # 模块 2：KSP 处理器
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── com/company/processor/
│           ├── MyProcessor.kt       # KSP 处理器
│           ├── MyProcessorProvider.kt
│           └── codegen/
│               └── CodeGenerator.kt # 代码生成逻辑
│
├── plugin/                          # 模块 3：Gradle 插件（ASM）
│   ├── build.gradle.kts
│   ├── settings.gradle.kts          # 独立 settings（如果用 includeBuild）
│   └── src/main/kotlin/
│       └── com/company/plugin/
│           ├── MyPlugin.kt          # Gradle 插件入口
│           ├── MyExtension.kt       # 配置扩展
│           ├── MyTransform.kt       # ASM Transform（旧 API）
│           └── asm/
│               ├── ClassVisitorFactory.kt
│               └── MethodVisitor.kt
│
├── runtime/                         # 模块 4：运行时库（可选）
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── com/company/runtime/
│           └── RuntimeHelper.kt     # 运行时辅助类
│
└── sample/                          # 模块 5：示例应用
    ├── build.gradle.kts
    └── src/main/kotlin/
        └── com/company/sample/
            └── MainActivity.kt
```

**特点**：
- ✅ 所有模块在同一仓库
- ✅ 易于开发和调试
- ✅ 版本统一管理
- ✅ 适合中小型项目

---

### 方案 B：多仓库（企业级）⭐⭐⭐⭐

```
company-annotation/                  # 仓库 1：注解
├── build.gradle.kts
└── src/main/kotlin/
    └── com/company/annotation/
        └── MyAnnotation.kt

company-processor/                   # 仓库 2：KSP 处理器
├── build.gradle.kts
└── src/main/kotlin/
    └── com/company/processor/
        └── MyProcessor.kt

company-plugin/                      # 仓库 3：Gradle 插件
├── build.gradle.kts
└── src/main/kotlin/
    └── com/company/plugin/
        └── MyPlugin.kt

company-sample/                      # 仓库 4：示例应用
├── build.gradle.kts
└── src/main/kotlin/
```

**特点**：
- ✅ 完全解耦
- ✅ 独立发布
- ✅ 团队分工明确
- ⚠️ 调试麻烦
- ⚠️ 版本协调复杂

---

## 🏗️ 详细结构（以单仓库为例）

### 1️⃣ annotation/ - 注解模块

```
annotation/
├── build.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── com/company/annotation/
                ├── AutoInit.kt          # 主注解
                ├── Generated.kt         # 标记生成的代码
                └── internal/
                    └── Metadata.kt      # 内部元数据注解
```

**build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm")
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.company"
            artifactId = "annotation"
            version = "1.0.0"
        }
    }
}
```

---

### 2️⃣ processor/ - KSP 处理器模块

```
processor/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/company/processor/
    │   │       ├── AutoInitProcessor.kt        # 主处理器
    │   │       ├── AutoInitProcessorProvider.kt
    │   │       ├── visitor/
    │   │       │   ├── ClassVisitor.kt
    │   │       │   └── FunctionVisitor.kt
    │   │       └── codegen/
    │   │           ├── InitializerGenerator.kt  # 生成初始化代码
    │   │           └── RegistryGenerator.kt     # 生成注册表
    │   └── resources/
    │       └── META-INF/
    │           └── services/
    │               └── com.google.devtools.ksp.processing.SymbolProcessorProvider
    │
    └── test/
        └── kotlin/
            └── com/company/processor/
                └── AutoInitProcessorTest.kt
```

**build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":annotation"))          // 依赖注解
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.20-1.0.14")
    implementation("com.squareup:kotlinpoet:1.14.2")  // 代码生成
    
    testImplementation("com.google.devtools.ksp:symbol-processing:1.9.20-1.0.14")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.5.0")
}
```

**服务注册文件**:
```
com.company.processor.AutoInitProcessorProvider
```

---

### 3️⃣ plugin/ - Gradle 插件模块

```
plugin/
├── build.gradle.kts
├── settings.gradle.kts              # 如果使用 includeBuild
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/company/plugin/
    │   │       ├── AutoInitPlugin.kt           # 插件入口
    │   │       ├── AutoInitExtension.kt        # 配置扩展
    │   │       ├── transform/
    │   │       │   ├── AutoInitTransform.kt    # Transform 任务
    │   │       │   └── ClassVisitorFactory.kt
    │   │       └── asm/
    │   │           ├── InitMethodVisitor.kt    # ASM 方法访问器
    │   │           └── RegistryInjector.kt     # 注册表注入器
    │   └── resources/
    │       └── META-INF/
    │           └── gradle-plugins/
    │               └── com.company.autoinit.properties
    │
    └── test/
        └── kotlin/
            └── com/company/plugin/
                └── AutoInitPluginTest.kt
```

**build.gradle.kts**:
```kotlin
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    compileOnly(project(":annotation"))         // 可选：用于类型安全
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
}

gradlePlugin {
    plugins {
        create("autoInitPlugin") {
            id = "com.company.autoinit"
            implementationClass = "com.company.plugin.AutoInitPlugin"
        }
    }
}
```

---

### 4️⃣ runtime/ - 运行时库（可选）

```
runtime/
├── build.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── com/company/runtime/
                ├── AutoInitRegistry.kt      # 自动初始化注册表
                ├── InitContext.kt           # 初始化上下文
                └── Logger.kt                # 日志工具
```

**build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":annotation"))
}
```

---

### 5️⃣ sample/ - 示例应用

```
sample/
├── build.gradle.kts
└── src/
    └── main/
        ├── kotlin/
        │   └── com/company/sample/
        │       ├── MainActivity.kt
        │       ├── MyApplication.kt
        │       └── modules/
        │           ├── NetworkModule.kt      # 带 @AutoInit 的模块
        │           └── DatabaseModule.kt
        └── AndroidManifest.xml
```

**build.gradle.kts**:
```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
    id("com.company.autoinit")              // 应用插件
}

dependencies {
    implementation(project(":annotation"))   // 注解
    ksp(project(":processor"))              // KSP 处理器
    implementation(project(":runtime"))      // 运行时库
}

autoInit {
    enabled = true
    generateRegistry = true
}
```

---

## 📊 模块依赖关系图

```
┌─────────────┐
│ annotation  │ ◄────────┐
└─────────────┘          │
       ▲                 │
       │                 │
       ├─────────────────┼─────────┐
       │                 │         │
┌─────────────┐   ┌─────────────┐ │
│  processor  │   │   plugin    │ │
└─────────────┘   └─────────────┘ │
       ▲                 ▲         │
       │                 │         │
       │                 │         │
       └─────────┬───────┘         │
                 │                 │
          ┌─────────────┐   ┌─────────────┐
          │   sample    │   │   runtime   │
          └─────────────┘   └─────────────┘
```

**说明**：
- `annotation` - 纯接口，无依赖
- `processor` - 依赖 `annotation` + KSP API
- `plugin` - 依赖 `annotation`（可选）+ AGP + ASM
- `runtime` - 依赖 `annotation`
- `sample` - 依赖所有模块

---

## 🎯 真实案例分析

### 案例 1: Hilt（Google）

```
hilt/
├── hilt-android/                    # 注解 + 运行时
│   └── src/main/java/dagger/hilt/
│       ├── android/
│       │   ├── AndroidEntryPoint.kt
│       │   └── HiltAndroidApp.kt
│       └── GeneratesRootInput.kt
│
├── hilt-compiler/                   # APT/KSP 处理器
│   └── src/main/java/dagger/hilt/
│       └── processor/
│           └── internal/
│               ├── ComponentGenerator.kt
│               └── RootGenerator.kt
│
└── hilt-android-gradle-plugin/      # Gradle 插件
    └── src/main/kotlin/dagger/hilt/
        └── android/plugin/
            └── HiltGradlePlugin.kt
```

**特点**：
- 注解和运行时合并在一个模块
- 处理器独立
- 插件独立

---

### 案例 2: Booster（字节跳动）

```
booster/
├── booster-api/                     # 注解
├── booster-transform-*/             # 多个 Transform（类似 KSP）
│   ├── booster-transform-activity-thread/
│   ├── booster-transform-thread/
│   └── booster-transform-toast/
├── booster-gradle-plugin/           # Gradle 插件
└── booster-android-*/               # 运行时库
```

**特点**：
- 按功能拆分多个 Transform
- 每个 Transform 是独立模块
- 统一的 Gradle 插件

---

### 案例 3: Matrix（腾讯）

```
matrix/
├── matrix-android-lib/              # 注解 + 运行时
├── matrix-gradle-plugin/            # Gradle 插件
│   └── src/main/kotlin/com/tencent/matrix/
│       ├── trace/
│       │   └── TraceBuildPlugin.kt
│       └── coverage/
│           └── CoverageBuildPlugin.kt
└── matrix-trace-canary/             # 监控实现
```

**特点**：
- 注解和运行时合并
- 插件按功能拆分子插件
- 监控实现独立

---

## 🔧 根项目配置

### settings.gradle.kts

```kotlin
pluginManagement {
    // 如果 plugin 使用 includeBuild
    includeBuild("plugin")
    
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AutoInit"

// 包含所有子模块
include(":annotation")
include(":processor")
include(":runtime")
include(":sample")
// plugin 通过 includeBuild 引入，不需要 include
```

### build.gradle.kts（根项目）

```kotlin
// Top-level build file
plugins {
    kotlin("jvm") version "1.9.20" apply false
    kotlin("android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    id("com.android.application") version "8.2.0" apply false
}

// 全局配置
subprojects {
    group = "com.company.autoinit"
    version = "1.0.0"
}
```

---

## 📦 发布配置

### Maven 发布

```kotlin
// 每个模块的 build.gradle.kts
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.company"
            artifactId = project.name
            version = "1.0.0"
            
            from(components["java"])
            
            pom {
                name.set("AutoInit ${project.name}")
                description.set("Automatic initialization framework")
                url.set("https://github.com/company/autoinit")
            }
        }
    }
    
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/company/autoinit")
        }
    }
}
```

### 版本管理

```kotlin
// gradle/libs.versions.toml
[versions]
kotlin = "1.9.20"
ksp = "1.9.20-1.0.14"
agp = "8.2.0"
asm = "9.6"

[libraries]
ksp-api = { module = "com.google.devtools.ksp:symbol-processing-api", version.ref = "ksp" }
agp = { module = "com.android.tools.build:gradle", version.ref = "agp" }
asm = { module = "org.ow2.asm:asm", version.ref = "asm" }
asm-commons = { module = "org.ow2.asm:asm-commons", version.ref = "asm" }

[plugins]
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## 🎓 最佳实践

### 1. 模块命名规范

| 模块类型 | 命名规范 | 示例 |
|---------|---------|------|
| 注解 | `*-annotation` 或 `*-api` | `autoinit-annotation` |
| 处理器 | `*-processor` 或 `*-compiler` | `autoinit-processor` |
| 插件 | `*-plugin` 或 `*-gradle-plugin` | `autoinit-plugin` |
| 运行时 | `*-runtime` 或无后缀 | `autoinit-runtime` |
| 示例 | `sample` 或 `demo` | `sample-android` |

### 2. 包名规范

```
com.company.library/
├── annotation/              # 注解包
│   ├── AutoInit.kt
│   └── Generated.kt
├── processor/               # 处理器包
│   ├── AutoInitProcessor.kt
│   └── codegen/
├── plugin/                  # 插件包
│   ├── AutoInitPlugin.kt
│   └── asm/
└── runtime/                 # 运行时包
    └── Registry.kt
```

### 3. 版本管理

```kotlin
// 方式 1：统一版本（推荐单仓库）
group = "com.company.autoinit"
version = "1.0.0"  // 所有模块同版本

// 方式 2：独立版本（适合多仓库）
// annotation: 1.0.0
// processor: 1.0.1
// plugin: 1.0.2
```

### 4. 文档结构

```
project-root/
├── README.md                # 项目总览
├── docs/
│   ├── getting-started.md   # 快速开始
│   ├── configuration.md     # 配置说明
│   └── architecture.md      # 架构设计
├── annotation/
│   └── README.md            # 注解使用说明
├── processor/
│   └── README.md            # 处理器开发说明
└── plugin/
    └── README.md            # 插件配置说明
```

---

## 📝 总结

### 推荐结构（单仓库）

```
project-root/
├── annotation/      # 轻量级，无依赖
├── processor/       # KSP 处理器，生成代码
├── plugin/          # Gradle 插件，ASM 字节码增强
├── runtime/         # 运行时支持（可选）
└── sample/          # 示例应用
```

**优势**：
- ✅ 所有模块在同一仓库，便于开发
- ✅ 版本统一管理
- ✅ 易于调试和测试
- ✅ 适合 80% 的项目

### 关键点

1. **annotation** - 保持纯净，无依赖
2. **processor** - 依赖 annotation + KSP API
3. **plugin** - 独立构建（includeBuild）或子模块
4. **runtime** - 可选，提供运行时支持
5. **sample** - 用于测试和示例

**你的 Timing 项目已经采用了这种标准结构！** ✅
