plugins {
  id("cshisan.android.library")
  id("cshisan.android.hilt")
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.google.secrets.get().pluginId)
  id("cshisan.spotless")
}

android {
  namespace = "com.cshisan.core.repository"

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  api(projects.core.model)

  implementation(libs.androidx.material3.android)

  // firebase
  implementation(platform(libs.firebase.bom))
  implementation(libs.firebase.auth)
  implementation(libs.firebase.store)
  implementation(libs.firebase.remote.config)
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "secrets.defaults.properties"
}