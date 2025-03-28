plugins {
  id("cshisan.android.application")
  id("cshisan.android.application.compose")
  id("cshisan.android.hilt")
  id("cshisan.spotless")
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "com.cshisan"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.cshisan"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
  }
  buildFeatures {
    compose = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.material3.android)
  androidTestImplementation(libs.androidx.espresso.core)

  // Cores
  implementation(projects.core.api)
  implementation(projects.core.model)
  implementation(projects.core.repository)

  // Features
  implementation(projects.feature.components)
  implementation(projects.feature.chat)
  implementation(projects.feature.login)
  implementation(projects.feature.nav)
  implementation(projects.feature.setting)

  // Compose
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material.iconsExtended)
  
  // Core Splash Screen
  implementation(libs.androidx.core.splashscreen)
  
  // Coroutines
  implementation(libs.kotlinx.coroutines.android)

  // Serialization
  implementation(libs.kotlinx.serialization.json)
}