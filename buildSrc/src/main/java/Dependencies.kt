import org.gradle.kotlin.dsl.DependencyHandlerScope

object Dependencies {
    object Kotlin {
        const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions2.KOTLIN}"
    }

    object AndroidX {
        const val CORE = "androidx.core:core-ktx:${Versions2.AndroidX.CORE}"
        const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions2.AndroidX.APPCOMPAT}"
    }

    object Google {
        const val MATERIAL = "com.google.android.material:material:${Versions2.Google.MATERIAL}"
    }

    object Test {
        object Unit {
            const val JUNIT = "junit:junit:${Versions2.Test.JUNIT}"
        }

        object Integration {
            const val JUNIT = "androidx.test.ext:junit:${Versions2.Test.JUNIT_INTEGRATION}"
            const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions2.Test.ESPRESSO}"
        }
    }

    fun DependencyHandlerScope.common() {
        "implementation"(Kotlin.STDLIB)
        "implementation"(AndroidX.CORE)
    }

    fun DependencyHandlerScope.commonIntegrationTest() {
        "androidTestImplementation"(Test.Integration.JUNIT)
        "androidTestImplementation"(Test.Integration.ESPRESSO_CORE)
    }
}