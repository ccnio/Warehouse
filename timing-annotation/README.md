# Timing Annotation Module

这是一个轻量级的注解模块，包含 `@Timing` 注解。

## 📦 模块说明

- **职责**：只包含 `@Timing` 注解定义
- **依赖**：无外部依赖，纯 Kotlin 标准库
- **大小**：非常小，不会增加应用体积

## 📖 使用方式

### 1. 添加依赖

在你的应用模块 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":timing-plugin:annotation"))
}
```

### 2. 应用插件

```kotlin
plugins {
    id("com.ccino.timing")
}

timing {
    annotationClass = "com.ccino.timing.annotation.Timing"
}
```

### 3. 使用注解

```kotlin
import com.ccino.timing.annotation.Timing

class MainActivity : AppCompatActivity() {
    @Timing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 方法逻辑
    }
}
```

## 🏗️ 架构设计

```
timing-plugin/
├── annotation/          # 注解模块（应用依赖这个）
│   └── Timing.kt       # @Timing 注解定义
└── plugin/             # Gradle 插件（编译时使用）
    ├── TimingPlugin.kt
    └── ...
```

## ✨ 优点

1. **职责分离**：注解和插件分离，符合单一职责原则
2. **依赖轻量**：应用只需依赖注解模块，不需要引入插件代码
3. **便于维护**：注解和实现分离，易于独立演进
4. **可复用**：注解模块可以单独发布到 Maven 仓库

## 📝 技术细节

- **Retention**: `BINARY` - 注解保留在 `.class` 文件中，ASM 可以读取
- **Target**: `FUNCTION` - 只能标注在方法上
- **兼容性**: 支持 Java 11+
