# Timing 注解迁移说明

## ✅ 完成的工作

### 1. 创建独立的注解模块

已将 `@Timing` 注解从 `kspDemo` 移至独立的 `timing-annotation` 模块：

```
Warehouse/
├── timing-annotation/          # 新建：独立的注解模块
│   ├── build.gradle.kts
│   ├── README.md
│   └── src/main/kotlin/com/ccino/timing/annotation/
│       └── Timing.kt          # @Timing 注解（新路径）
│
├── timing-plugin/             # Gradle 插件和 ASM 实现
│   ├── src/main/kotlin/com/ccino/timing/plugin/
│   └── ...
│
└── kspDemo/                   # 只保留 Extract 相关的 KSP 功能
    └── src/main/java/com/ccino/ksp/
        ├── extract/           # ExtractorInterface 功能
        └── bindview/          # BindView 功能
```

### 2. 删除的文件

- ❌ `kspDemo/src/main/java/com/ccino/ksp/timing/Timing.kt`（旧位置）
- ❌ `kspDemo/src/main/java/com/ccino/ksp/timing/TimingProcessor.kt`（不再需要的 KSP 处理器）
- ❌ `kspDemo/src/main/java/com/ccino/ksp/timing/TimingProcessorProvider.kt`

### 3. 新增的文件

- ✅ `timing-annotation/build.gradle.kts`
- ✅ `timing-annotation/README.md`
- ✅ `timing-annotation/src/main/kotlin/com/ccino/timing/annotation/Timing.kt`

### 4. 更新的配置

#### `settings.gradle.kts`（根项目）
```kotlin
include(":timing-annotation")  // 新增
```

#### `demo/build.gradle.kts`
```kotlin
dependencies {
    implementation(project(":timing-annotation"))  // 新增：引用注解模块
}

timing {
    annotationClass = "com.ccino.timing.annotation.Timing"  // 更新路径
}
```

#### 更新的导入语句
所有使用 `@Timing` 的文件都已更新导入：
```kotlin
// 旧：import com.ccino.ksp.timing.Timing
// 新：
import com.ccino.timing.annotation.Timing
```

更新的文件包括：
- `demo/src/main/java/com/ccino/demo/MainActivity.kt`
- `demo/src/main/java/com/ccino/demo/TimingTestActivity.kt`
- `demo/src/main/java/com/ccino/demo/TimingExample.kt`

### 5. 插件修复

修复了 `TimingPlugin` 中的参数注解问题：
```kotlin
interface TimingParams : InstrumentationParameters {
    @get:org.gradle.api.tasks.Input
    val annotationDescriptor: Property<String>
    
    @get:org.gradle.api.tasks.Input
    @get:org.gradle.api.tasks.Optional
    val logTag: Property<String?>
    
    @get:org.gradle.api.tasks.Input
    val minDuration: Property<Long>
    
    @get:org.gradle.api.tasks.Input
    val enabled: Property<Boolean>
}
```

## 📋 架构优势

### 职责分离
- **timing-annotation**: 纯注解定义，无依赖，体积极小
- **timing-plugin**: ASM 字节码插桩实现
- **kspDemo**: 只负责其他 KSP 功能（Extract、BindView）

### 解耦合
- 应用只需依赖轻量的 `timing-annotation` 模块
- 不需要引入整个 `kspDemo` 模块
- 插件和注解在同一项目下，便于管理

### 可配置
```kotlin
timing {
    annotationClass = "com.ccino.timing.annotation.Timing"  // 可配置注解路径
    logTag = "Performance"                                   // 可自定义 TAG
    minDuration = 10                                         // 可设置阈值
}
```

## 🎯 使用方式

### 1. 添加依赖
```kotlin
dependencies {
    implementation(project(":timing-annotation"))
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

class MyActivity : AppCompatActivity() {
    @Timing
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 插件会自动插入计时代码
    }
}
```

## ⚠️ 当前状态

- ✅ 注解模块创建成功
- ✅ 目录结构调整完成
- ✅ 所有导入路径更新完成
- ✅ 插件参数注解修复完成
- ⚠️ Demo 应用存在 Hilt 版本兼容性问题（与本次迁移无关）

## 📝 后续建议

如果需要解决 Hilt 问题，可以尝试：
1. 升级 Hilt 到最新版本
2. 或者降级 Kotlin 版本
3. 或者等待 Hilt 官方修复兼容性问题
