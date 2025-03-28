package com.cshisan.compiler.plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * Configure base Kotlin with Android options
 */
internal fun Project.configureKotlinAndroid(
  commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
  commonExtension.apply {
    compileSdk = 35

    defaultConfig {
      minSdk = 26
    }

    compileOptions {
      sourceCompatibility = JavaVersion.VERSION_21
      targetCompatibility = JavaVersion.VERSION_21
    }

    lint {
      abortOnError = false
    }


    tasks.withType<KotlinJvmCompile>().configureEach {
      compilerOptions {
        // Treat all Kotlin warnings as errors (disabled by default)
        allWarningsAsErrors.set(properties["warningsAsErrors"] as? Boolean ?: false)

        freeCompilerArgs.addAll(
          "-opt-in=kotlin.RequiresOptIn",
          // Enable experimental coroutines APIs, including Flow
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          // Enable experimental compose APIs
          "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
          "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
        )

        jvmTarget.set(JvmTarget.JVM_21)
      }
    }
  }
}