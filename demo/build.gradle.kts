plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.benchmark") version "1.3.3" apply false
}
android {
    namespace = "com.ccino.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ccino.demo"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    ksp {
        arg("parameter", "paramValue")
    }
}

dependencies {
    implementation(libs.androidx.swiperefreshlayout)
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.gson)
    implementation(libs.constraintlayout)
    implementation(project(":kspDemo")) // 为了能够引入注解
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.exoplayer)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.compose.foundation)

    "ksp"(project(":kspDemo")) // 为了生成代码
    implementation(libs.androidx.paging.runtime)
    implementation(libs.retrofit)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)
    implementation(libs.lottie)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation("io.insert-koin:koin-android:3.2.2")
    implementation("io.coil-kt.coil3:coil:3.2.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.2.0")
//    implementation("io.coil-kt.coil3:coil-transformations:3.2.0")

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Jetpack Benchmark
    androidTestImplementation("androidx.benchmark:benchmark-junit4:1.3.3")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    //使用 ksp 和 moshi-kotlin-codegen 是强烈推荐的最佳实践。它通过在编译时生成适配器（Adapter）来避免运行时反射，从而大幅提升性能和健壮性。
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
}