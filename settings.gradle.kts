pluginManagement {
    // 将 timing-plugin 作为复合构建引入（用于插件解析）
    includeBuild("timing-plugin")
    
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Warehouse"
//include(":app")
include(":demo")
include(":kspDemo")
include(":timing-annotation")  // Timing 注解模块
//include(":todo")

// 注意：timing-plugin 通过 includeBuild 引入，不需要 include
