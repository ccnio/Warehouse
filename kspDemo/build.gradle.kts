plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//}

dependencies {
    implementation(libs.symbol.processing.api)//引入ksp
    implementation(libs.kotlinpoet)
    implementation(libs.kotlinpoet.ksp)
}

// 兼容旧版 DSL，确保 jvmTarget 为 17
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}