plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`  // 可选：用于发布到 Maven
}

dependencies {
    // AGP 和 ASM 依赖
    implementation("com.android.tools.build:gradle:8.2.0")
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    
    // 注意：由于 timing-plugin 是 includeBuild，无法直接依赖外部 project
    // 所以我们使用字符串默认值，但提供合理的默认配置
}

gradlePlugin {
    plugins {
        create("timingPlugin") {
            id = "com.ccino.timing"
            implementationClass = "com.ccino.timing.plugin.TimingPlugin"
            displayName = "Timing Plugin"
            description = "A Gradle plugin for automatic method timing using ASM bytecode instrumentation"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// 可选：配置发布到 Maven
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.ccino"
            artifactId = "timing-plugin"
            version = "1.0.0"
        }
    }
}
