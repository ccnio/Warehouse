# Timing 项目结构对比 - 当前 vs KSP+Plugin 组合

## 📊 当前 Timing 项目结构（纯 Plugin）

```
Warehouse/                           # 根项目
│
├── timing-annotation/               ✅ 注解模块
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       └── com/ccino/timing/annotation/
│           └── Timing.kt            # @Timing 注解
│
├── timing-plugin/                   ✅ Gradle 插件（ASM）
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── src/main/kotlin/
│       └── com/ccino/timing/plugin/
│           ├── TimingPlugin.kt
│           ├── TimingExtension.kt
│           ├── TimingClassVisitorFactory.kt
│           └── TimingMethodVisitor.kt
│
└── demo/                            ✅ 示例应用
    ├── build.gradle.kts
    └── src/main/java/
        └── com/ccino/demo/
            └── MainActivity.kt
```

**特点**：
- ✅ 纯 Plugin 方案，使用 ASM 字节码增强
- ✅ 结构简洁，2 个核心模块
- ✅ 适合简单的 AOP 场景
- ✅ 用户只需加注解，无需其他代码

---

## 🔄 如果扩展为 KSP + Plugin 组合

### 场景：需要生成初始化代码

假设我们要扩展 Timing 功能：
- KSP：生成性能统计报告类
- Plugin：在方法前后插入计时代码

### 推荐结构 A：添加 processor 模块

```
Warehouse/
│
├── timing-annotation/               # 模块 1：注解（不变）
│   └── src/main/kotlin/
│       └── com/ccino/timing/annotation/
│           ├── Timing.kt            # 原有注解
│           └── TimingReport.kt      # ✨ 新增：生成报告注解
│
├── timing-processor/                # ✨ 模块 2：KSP 处理器（新增）
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── kotlin/
│       │   │   └── com/ccino/timing/processor/
│       │   │       ├── TimingProcessor.kt
│       │   │       ├── TimingProcessorProvider.kt
│       │   │       └── codegen/
│       │   │           ├── ReportGenerator.kt      # 生成报告类
│       │   │           └── StatisticsGenerator.kt   # 生成统计类
│       │   └── resources/
│       │       └── META-INF/services/
│       │           └── com.google.devtools.ksp.processing.SymbolProcessorProvider
│       └── test/
│           └── kotlin/
│
├── timing-plugin/                   # 模块 3：Gradle 插件（保持）
│   └── src/main/kotlin/
│       └── com/ccino/timing/plugin/
│           ├── TimingPlugin.kt
│           └── asm/
│               ├── TimingMethodVisitor.kt
│               └── ReportInjector.kt   # ✨ 新增：注入报告收集代码
│
├── timing-runtime/                  # ✨ 模块 4：运行时库（新增）
│   └── src/main/kotlin/
│       └── com/ccino/timing/runtime/
│           ├── TimingRegistry.kt    # 统计数据注册表
│           ├── TimingCollector.kt   # 数据收集器
│           └── ReportExporter.kt    # 报告导出器
│
└── demo/                            # 模块 5：示例应用（更新）
    └── build.gradle.kts
        plugins {
            id("com.google.devtools.ksp")    # ✨ 新增 KSP
            id("com.ccino.timing")
        }
        dependencies {
            implementation(project(":timing-annotation"))
            ksp(project(":timing-processor"))        # ✨ KSP 处理
            implementation(project(":timing-runtime"))  # ✨ 运行时
        }
```

---

## 📝 详细模块说明

### 1. timing-annotation（保持 + 扩展）

```kotlin
// Timing.kt（原有）
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Timing

// TimingReport.kt（新增）
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)  // KSP 处理
annotation class TimingReport(
    val category: String = "default",
    val enabled: Boolean = true
)
```

---

### 2. timing-processor（新增）

**build.gradle.kts**:
```kotlin
plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":timing-annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.20-1.0.14")
    implementation("com.squareup:kotlinpoet:1.14.2")
}
```

**TimingProcessor.kt**:
```kotlin
class TimingProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    
    override fun process(resolver: Resolver): List<KSAnnotated> {
        // 1. 查找所有带 @TimingReport 的类
        val reportClasses = resolver.getSymbolsWithAnnotation(
            "com.ccino.timing.annotation.TimingReport"
        )
        
        // 2. 生成报告收集类
        reportClasses.forEach { symbol ->
            generateReportClass(symbol)
        }
        
        return emptyList()
    }
    
    private fun generateReportClass(symbol: KSAnnotated) {
        val className = symbol.simpleName.asString()
        
        // 使用 KotlinPoet 生成代码
        val file = FileSpec.builder("com.ccino.timing.generated", "${className}Report")
            .addType(
                TypeSpec.objectBuilder("${className}Report")
                    .addProperty(
                        PropertySpec.builder("timings", MAP)
                            .initializer("mutableMapOf<String, Long>()")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("record")
                            .addParameter("method", String::class)
                            .addParameter("duration", Long::class)
                            .addStatement("timings[method] = duration")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("export")
                            .returns(String::class)
                            .addStatement("return timings.toString()")
                            .build()
                    )
                    .build()
            )
            .build()
        
        // 写入文件
        codeGenerator.createNewFile(
            Dependencies(false),
            "com.ccino.timing.generated",
            "${className}Report"
        ).bufferedWriter().use { file.writeTo(it) }
    }
}
```

---

### 3. timing-plugin（扩展）

**ReportInjector.kt**（新增）:
```kotlin
class ReportInjector(
    methodVisitor: MethodVisitor,
    access: Int,
    methodName: String,
    descriptor: String,
    private val className: String
) : AdviceAdapter(ASM9, methodVisitor, access, methodName, descriptor) {
    
    override fun onMethodExit(opcode: Int) {
        // 注入代码：MainActivityReport.record("onCreate", duration)
        mv.visitLdcInsn(methodName)
        mv.visitVarInsn(LLOAD, durationVar)
        mv.visitMethodInsn(
            INVOKESTATIC,
            "${className}Report",
            "record",
            "(Ljava/lang/String;J)V",
            false
        )
        
        super.onMethodExit(opcode)
    }
}
```

---

### 4. timing-runtime（新增）

**TimingRegistry.kt**:
```kotlin
object TimingRegistry {
    private val reports = mutableMapOf<String, TimingReport>()
    
    fun registerReport(name: String, report: TimingReport) {
        reports[name] = report
    }
    
    fun exportAll(): String {
        return reports.entries.joinToString("\n") { (name, report) ->
            "$name:\n${report.export()}"
        }
    }
}

interface TimingReport {
    fun record(method: String, duration: Long)
    fun export(): String
}
```

---

### 5. demo/（使用示例）

```kotlin
@TimingReport(category = "UI")  // ✨ KSP 生成报告类
class MainActivity : AppCompatActivity() {
    
    @Timing  // ✨ Plugin 插入计时代码
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
    }
    
    @Timing
    fun loadData() {
        // ...
    }
    
    fun exportReport() {
        // KSP 生成的代码
        val report = MainActivityReport.export()
        Log.d("Report", report)
    }
}
```

---

## 🔄 工作流程对比

### 当前（纯 Plugin）

```
源代码(.kt)
    ↓
    ├─ @Timing 注解
    ↓
编译时（Plugin ASM）
    ├─ 插入计时代码
    ↓
字节码(.class)
    ↓
运行时
    └─ Log.d("ClassName", "method: consume = xxx ms")
```

---

### 扩展后（KSP + Plugin）

```
源代码(.kt)
    ↓
    ├─ @TimingReport 注解（类级别）
    ├─ @Timing 注解（方法级别）
    ↓
编译阶段 1: KSP 处理
    ├─ 扫描 @TimingReport
    ├─ 生成 MainActivityReport.kt
    └─ 生成报告收集代码
    ↓
生成的代码 + 原始代码
    ↓
编译阶段 2: Kotlin 编译
    ├─ 编译所有 .kt → .class
    ↓
编译阶段 3: Plugin ASM
    ├─ 扫描 @Timing
    ├─ 插入计时代码
    └─ 插入报告收集调用
    ↓
最终字节码(.class)
    ↓
运行时
    ├─ 计时
    ├─ 收集数据到 Report
    └─ 可以导出完整报告
```

---

## 📊 功能对比

| 功能 | 纯 Plugin | KSP + Plugin |
|------|----------|--------------|
| **计时** | ✅ Plugin | ✅ Plugin |
| **日志输出** | ✅ Plugin | ✅ Plugin |
| **生成报告类** | ❌ | ✅ KSP |
| **统计分析** | ❌ | ✅ KSP |
| **数据聚合** | ❌ | ✅ Runtime |
| **编译复杂度** | 简单 ⚡ | 中等 |
| **运行时开销** | 低 | 低 |

---

## 🎯 何时需要 KSP？

### ❌ 不需要 KSP 的场景

1. **简单的 AOP** - 只是在方法前后加代码
   - 例如：计时、日志、权限检查
   - ✅ 纯 Plugin 就够了

2. **无需生成代码** - 不需要创建新类
   - 例如：修改方法行为、替换方法调用
   - ✅ 纯 Plugin 就够了

### ✅ 需要 KSP 的场景

1. **生成辅助类** - 需要创建新的类
   - 例如：生成 Builder、Adapter、Proxy
   - ✅ 需要 KSP

2. **复杂的代码生成** - 需要分析代码结构
   - 例如：根据接口生成实现、生成路由表
   - ✅ 需要 KSP

3. **需要类型信息** - 需要知道参数类型、返回值
   - 例如：生成序列化代码、生成 DAO
   - ✅ 需要 KSP

4. **需要用户调用生成的代码**
   - 例如：生成的初始化方法需要手动调用
   - ✅ 需要 KSP（生成）+ Plugin（自动调用）

---

## 💡 你的 Timing 项目分析

### 当前需求 ✅

```kotlin
@Timing
fun myMethod() {
    // 原始代码
}

// 期望：自动打印耗时日志
// D/ClassName: myMethod: consume = 100 ms
```

**结论**：✅ **纯 Plugin 完美满足，不需要 KSP**

---

### 潜在扩展需求 🤔

如果将来需要：

1. **生成统计报告类** ✨
   ```kotlin
   @TimingReport
   class MainActivity {
       @Timing fun onCreate() { }
       @Timing fun onResume() { }
   }
   
   // 需要生成：
   object MainActivityReport {
       fun exportReport(): String
   }
   ```
   → 需要 KSP

2. **生成性能分析工具** ✨
   ```kotlin
   // 需要生成：
   object PerformanceAnalyzer {
       fun getSlowMethods(): List<String>
       fun exportToJson(): String
   }
   ```
   → 需要 KSP

3. **自动注册到监控平台** ✨
   ```kotlin
   // 需要生成初始化代码
   // Plugin 自动调用
   ```
   → 需要 KSP + Plugin

---

## 📝 迁移路径（如果需要）

### 第 1 阶段：保持当前结构 ✅
```
timing-annotation/
timing-plugin/
```
- 适用于 90% 的场景
- 简单、高效

### 第 2 阶段：添加 processor（如果需要生成代码）
```
timing-annotation/     （扩展注解）
timing-processor/      ✨ 新增
timing-plugin/         （扩展功能）
timing-runtime/        ✨ 新增（可选）
```
- 适用于复杂场景
- 需要生成代码

### 第 3 阶段：完整的 SDK（企业级）
```
timing-annotation/
timing-processor/
timing-plugin/
timing-runtime/
timing-analytics/      ✨ 分析工具
timing-dashboard/      ✨ 可视化面板
```

---

## 🎓 总结

### 你的 Timing 项目

**当前结构** ✅：
```
timing-annotation/  → 轻量级注解
timing-plugin/      → ASM 字节码增强
```

**优势**：
- ✅ 简单高效
- ✅ 满足 99% 的计时需求
- ✅ 用户体验好（只需加注解）
- ✅ 性能开销低

**何时扩展为 KSP + Plugin**：
- 需要生成统计报告类
- 需要复杂的性能分析
- 需要自动注册到监控平台

**建议**：
- 🎯 保持当前结构，不要过度设计
- 📝 如果将来需要，再添加 processor 模块
- 🚀 当前方案已经很优秀！

---

**你的项目结构已经符合业界标准！** ✅
