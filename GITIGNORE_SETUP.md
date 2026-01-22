# Git Ignore 配置说明

## 📁 已配置的忽略规则

### 1️⃣ 根目录 `.gitignore`

```gitignore
# Android 标准忽略规则
*.iml
.gradle
/local.properties
/.idea/
.DS_Store
/build
/captures
.externalNativeBuild
.cxx

# ✅ 新增：Timing 模块专用规则
timing-plugin/build/
timing-plugin/.gradle/
timing-annotation/build/
timing-annotation/.gradle/

# ✅ 新增：所有子模块的构建目录
**/build/
**/.gradle/
```

**说明**：
- `timing-plugin/build/` - 插件模块的构建输出
- `timing-plugin/.gradle/` - 插件模块的 Gradle 缓存
- `timing-annotation/build/` - 注解模块的构建输出
- `timing-annotation/.gradle/` - 注解模块的 Gradle 缓存
- `**/build/` - 匹配所有子目录的 build 文件夹
- `**/.gradle/` - 匹配所有子目录的 .gradle 文件夹

---

### 2️⃣ timing-plugin/.gitignore

```gitignore
# Gradle
.gradle/
build/

# IDE
.idea/
*.iml

# OS
.DS_Store
```

**说明**：
- 为 timing-plugin 模块单独配置忽略规则
- 因为它是 `includeBuild` 引入的独立项目

---

### 3️⃣ timing-annotation/.gitignore

```gitignore
# Gradle
.gradle/
build/

# IDE
.idea/
*.iml

# OS
.DS_Store
```

**说明**：
- 为 timing-annotation 模块单独配置忽略规则
- 保持模块的独立性

---

## 🎯 被忽略的文件/目录

### Timing 相关
```
timing-plugin/
  ├── .gradle/          ✅ 被忽略（Gradle 缓存）
  ├── build/            ✅ 被忽略（构建产物）
  │   ├── classes/
  │   ├── kotlin/
  │   ├── libs/
  │   └── tmp/
  └── .idea/            ✅ 被忽略（IDE 配置）

timing-annotation/
  ├── .gradle/          ✅ 被忽略（Gradle 缓存）
  ├── build/            ✅ 被忽略（构建产物）
  │   ├── classes/
  │   ├── kotlin/
  │   └── libs/
  └── .idea/            ✅ 被忽略（IDE 配置）
```

### 其他模块
```
demo/build/             ✅ 被忽略
kspDemo/build/          ✅ 被忽略
app/build/              ✅ 被忽略
libs/**/build/          ✅ 被忽略
```

---

## ✅ 被跟踪的文件

### 源代码文件
```
timing-plugin/
  ├── build.gradle.kts            ✅ 跟踪
  ├── settings.gradle.kts         ✅ 跟踪
  ├── .gitignore                  ✅ 跟踪
  ├── README.md                   ✅ 跟踪
  └── src/main/kotlin/            ✅ 跟踪
      └── com/ccino/timing/plugin/
          ├── TimingPlugin.kt
          ├── TimingExtension.kt
          └── ...

timing-annotation/
  ├── build.gradle.kts            ✅ 跟踪
  ├── .gitignore                  ✅ 跟踪
  ├── README.md                   ✅ 跟踪
  └── src/main/kotlin/            ✅ 跟踪
      └── com/ccino/timing/annotation/
          └── Timing.kt
```

---

## 🔍 验证配置

### 1. 查看被忽略的文件
```bash
git status --ignored
```

### 2. 检查特定目录
```bash
git check-ignore -v timing-plugin/build
git check-ignore -v timing-annotation/build
```

### 3. 查看未跟踪的文件
```bash
git status --porcelain
```

---

## 📝 规则优先级

Git ignore 规则的匹配顺序：

1. **子目录的 `.gitignore`** 优先级最高
   - `timing-plugin/.gitignore`
   - `timing-annotation/.gitignore`

2. **根目录的 `.gitignore`**
   - `/Warehouse/.gitignore`

3. **通配符规则**
   - `**/build/` 匹配所有子目录的 build

---

## ⚠️ 注意事项

### 1. 已经被跟踪的文件

如果某些文件之前已经被 Git 跟踪，添加到 `.gitignore` 后不会自动忽略。

**解决方法**：
```bash
# 从 Git 中删除（但保留本地文件）
git rm -r --cached timing-plugin/build
git rm -r --cached timing-annotation/build

# 提交删除
git commit -m "Remove build directories from Git"
```

### 2. 全局 gitignore

可以设置全局规则（可选）：
```bash
# 编辑全局配置
git config --global core.excludesfile ~/.gitignore_global

# 添加规则到 ~/.gitignore_global
echo "build/" >> ~/.gitignore_global
echo ".gradle/" >> ~/.gitignore_global
```

### 3. IDE 生成的文件

`.idea/` 目录已被忽略，但建议在团队中统一 IDE 配置：
- 代码格式化规则
- 检查规则
- 编码设置

---

## 🎯 最佳实践

### ✅ 应该忽略的文件
- ✅ 构建产物（`build/`, `*.jar`, `*.class`）
- ✅ Gradle 缓存（`.gradle/`）
- ✅ IDE 配置（`.idea/`, `*.iml`）
- ✅ 操作系统文件（`.DS_Store`, `Thumbs.db`）
- ✅ 本地配置（`local.properties`）

### ❌ 不应该忽略的文件
- ❌ 源代码（`*.kt`, `*.java`）
- ❌ 构建脚本（`build.gradle.kts`, `settings.gradle.kts`）
- ❌ 文档（`README.md`, `*.md`）
- ❌ 资源文件（`res/`, `assets/`）
- ❌ 依赖配置（`gradle/libs.versions.toml`）

---

## 📚 相关文档

- [Git Ignore 官方文档](https://git-scm.com/docs/gitignore)
- [Android .gitignore 模板](https://github.com/github/gitignore/blob/main/Android.gitignore)
- [Gradle .gitignore 最佳实践](https://docs.gradle.org/current/userguide/build_environment.html)

---

## ✅ 总结

**已完成的配置**：
1. ✅ 根目录添加了 Timing 模块的忽略规则
2. ✅ 为 timing-plugin 创建了独立的 `.gitignore`
3. ✅ 为 timing-annotation 创建了独立的 `.gitignore`
4. ✅ 使用通配符 `**/build/` 覆盖所有子模块

**效果**：
- 🚫 构建产物不会被提交到 Git
- ✅ 源代码和配置文件正常跟踪
- 🎯 保持仓库干净整洁
