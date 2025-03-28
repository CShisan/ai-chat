plugins {
  id("cshisan.android.library")
  id("cshisan.android.hilt")
  id("cshisan.spotless")
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.google.secrets.get().pluginId)
}

android {
  namespace = "com.cshisan.core.repository"

  buildFeatures {
    buildConfig = true
  }
}

dependencies {
  api(projects.core.model)

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