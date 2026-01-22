# Plugin 是否应该依赖 Annotation？深度分析

## 🤔 问题

现在有独立的 `timing-annotation` 模块，`timing-plugin` 是否可以直接依赖它，从而：
- ✅ 不需要 `annotationClass` 配置
- ✅ 类型安全，编译期检查
- ✅ 代码更简洁

## 📊 方案对比

### 方案 A：Plugin 依赖 Annotation（推荐）✅

```kotlin
// timing-plugin/build.gradle.kts
dependencies {
    compileOnly("com.ccino:timing-annotation:1.0.0")  // 只编译时使用
}

// Plugin 代码
import com.ccino.timing.annotation.Timing

class TimingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 不需要配置 annotationClass，直接使用
        val descriptor = "L${Timing::class.java.name.replace('.', '/')};"
    }
}

// 用户配置（简化）
timing {
    enabled = true
    // ❌ 不需要 annotationClass 了！
}
```

**优点**：
- ✅ 不需要手动配置注解路径
- ✅ 类型安全，编译期检查
- ✅ 注解改名会自动同步
- ✅ 代码更简洁
- ✅ 减少配置错误

**缺点**：
- ⚠️ Plugin 和 Annotation 版本耦合
- ⚠️ 灵活性稍差（不能用自定义注解）

---

### 方案 B：Plugin 不依赖（当前方案）

```kotlin
// 用户需要配置
timing {
    annotationClass = "com.ccino.timing.annotation.Timing"  // 手动配置
}
```

**优点**：
- ✅ Plugin 和 Annotation 完全解耦
- ✅ 可以支持任意包名的注解
- ✅ 灵活性最高

**缺点**：
- ❌ 需要手动配置，容易出错
- ❌ 没有编译期检查
- ❌ 注解改名需要手动同步

---

## 🎯 推荐方案：混合模式

**最佳实践**：Plugin 依赖 Annotation，但保留配置选项作为扩展点

```kotlin
// timing-plugin/build.gradle.kts
dependencies {
    compileOnly(project(":timing-annotation"))  // 编译时依赖
}

// TimingExtension.kt
open class TimingExtension {
    /**
     * 注解类的全限定名
     * 默认使用内置的 Timing 注解
     * 可以覆盖以支持自定义注解
     */
    var annotationClass: String = Timing::class.java.name  // 默认值
}

// TimingPlugin.kt
class TimingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("timing", TimingExtension::class.java)
        
        // 如果用户没有配置，使用默认的 Timing 注解
        val annotationDescriptor = "L${extension.annotationClass.replace('.', '/')};"
    }
}
```

**使用方式**：

```kotlin
// 1. 默认使用（推荐）✅
timing {
    enabled = true
    // 自动使用 com.ccino.timing.annotation.Timing
}

// 2. 自定义注解（高级用法）
timing {
    enabled = true
    annotationClass = "com.mycompany.MyTiming"  // 覆盖默认值
}
```

---

## 🏗️ 实现细节

### 问题：includeBuild 如何依赖根项目？

`timing-plugin` 是通过 `includeBuild` 引入的，它有独立的 build。要依赖根项目的 `timing-annotation`，有两种方式：

#### 方式 1：通过 includeBuild 引用（推荐）

```kotlin
// timing-plugin/settings.gradle.kts
rootProject.name = "timing-plugin"

// 引用父项目，以便访问 timing-annotation
includeBuild("../")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

```kotlin
// timing-plugin/build.gradle.kts
dependencies {
    compileOnly(project(":timing-annotation"))  // 可以引用了
}
```

#### 方式 2：使用已发布的版本（更解耦）

```kotlin
// timing-plugin/build.gradle.kts
dependencies {
    compileOnly("com.ccino:timing-annotation:1.0.0")  // Maven 坐标
}
```

---

## 📚 业界对比

### Hilt 的做法

```kotlin
// Hilt Plugin
dependencies {
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.x")
    // ❌ 不依赖 hilt-android（注解模块）
}

// 用户需要同时依赖
dependencies {
    implementation("com.google.dagger:hilt-android:2.x")  // 注解
    kapt("com.google.dagger:hilt-compiler:2.x")           // 处理器
}
```

**原因**：
- Plugin 和 注解/处理器 分离
- 用户可以选择不同版本
- 最大灵活性

### Room 的做法

```kotlin
// Room Plugin（如果有的话）
// ❌ 也不依赖 room-runtime
```

### AspectJ 的做法

```java
// AspectJ Plugin
dependencies {
    implementation("org.aspectj:aspectjtools:1.9.x")  // 依赖 AspectJ 运行时
}

// 用户只需依赖
dependencies {
    implementation("org.aspectj:aspectjrt:1.9.x")
}
```

**原因**：
- Plugin 需要 AspectJ 的类型信息
- 编译期织入需要访问注解定义

---

## 🎓 结论

### 推荐方案：Plugin 使用 `compileOnly` 依赖 Annotation

**为什么？**

1. **简化配置** ✅
   ```kotlin
   // 用户只需要
   timing {
       enabled = true  // 就这么简单
   }
   ```

2. **类型安全** ✅
   ```kotlin
   // Plugin 中
   val descriptor = "L${Timing::class.java.name.replace('.', '/')};"
   // 编译期检查，不会写错
   ```

3. **自动同步** ✅
   - 注解改名，Plugin 自动感知
   - 不需要手动维护配置

4. **保持灵活性** ✅
   - 仍然支持 `annotationClass` 配置
   - 高级用户可以自定义注解

### 实现步骤

1. **修改 timing-plugin/settings.gradle.kts**
   ```kotlin
   includeBuild("../")  // 引用父项目
   ```

2. **修改 timing-plugin/build.gradle.kts**
   ```kotlin
   dependencies {
       compileOnly(project(":timing-annotation"))
   }
   ```

3. **修改 TimingExtension.kt**
   ```kotlin
   var annotationClass: String = Timing::class.java.name  // 默认值
   ```

4. **简化用户配置**
   ```kotlin
   timing {
       enabled = true  // 够了！
   }
   ```

---

## 🔥 对比效果

### 之前（需要配置）

```kotlin
timing {
    enabled = true
    annotationClass = "com.ccino.timing.annotation.Timing"  // 必须配置
    logTag = null
    minDuration = 0
    instrumentDependencies = false
}
```

### 之后（自动推断）✨

```kotlin
timing {
    enabled = true  // 就这么简单！
    // annotationClass 自动使用默认值
    // 其他也都有合理默认值
}

// 高级配置（可选）
timing {
    enabled = true
    minDuration = 100  // 只配置需要的
}
```

---

## ⚖️ 权衡

| 方案 | 配置复杂度 | 灵活性 | 类型安全 | 推荐度 |
|------|----------|--------|---------|--------|
| **不依赖** | 高 ⚠️ | 最高 ✅ | 无 ❌ | ⭐⭐ |
| **依赖 + 保留配置** | 低 ✅ | 高 ✅ | 强 ✅ | ⭐⭐⭐⭐⭐ |
| **只依赖** | 最低 ✅ | 低 ⚠️ | 强 ✅ | ⭐⭐⭐⭐ |

---

## 🎯 最终建议

**✅ 采用"依赖 + 保留配置"方案**

**理由**：
1. ✅ 99% 的用户不需要配置 `annotationClass`
2. ✅ 1% 的高级用户仍然可以自定义
3. ✅ 类型安全，减少错误
4. ✅ 代码更简洁

**实施建议**：
1. Plugin 添加 `compileOnly` 依赖到 Annotation
2. 设置默认值：`annotationClass = Timing::class.java.name`
3. 文档说明：默认值，高级用法
4. 保持向后兼容

**用户体验**：
```kotlin
// ✨ 最简配置
timing {
    enabled = true
}

// Done! 开始使用 @Timing 注解
```

---

## 📝 总结

**你说得对！** 👍

Plugin 应该依赖 Annotation（使用 `compileOnly`），这样：
- ✅ 简化用户配置
- ✅ 提供类型安全
- ✅ 减少配置错误
- ✅ 保持灵活性（可选配置）

**这是更好的设计！** 🎉
