import org.gradle.api.JavaVersion

/**
 * Created by jianfeng.li on 2021/7/2.
 */
object Versions2 {
    object App {
        const val VERSION_CODE = 1
        const val VERSION_NAME = "1.0.0"
        const val MIN_SDK = 21
        const val TARGET_SDK = 29
        const val COMPILE_SDK = 29
    }

    object Google {
        const val MATERIAL = "1.3.0"
    }

    object AndroidX {
        const val CORE = "1.5.0"
        const val APPCOMPAT = "1.3.0"
    }

    object Test {
        const val JUNIT = "4.13.2"
        const val JUNIT_INTEGRATION = "1.1.2"
        const val ESPRESSO = "3.3.0"
    }

    // Make sure to update `buildSrc/build.gradle.kts` when updating this
    const val GRADLE = "4.2.1"

    // Make sure to update `buildSrc/build.gradle.kts` when updating this
    const val KOTLIN = "1.5.10"

    val JAVA = JavaVersion.VERSION_1_8
}