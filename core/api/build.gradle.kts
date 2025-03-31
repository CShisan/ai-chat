plugins {
  id("cshisan.android.library")
  id("cshisan.android.hilt")
  id("cshisan.spotless")
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "com.cshisan.core.api"
}

dependencies {
  api(projects.core.model)
  api(projects.core.common)

  implementation(libs.ktor.core)
  implementation(libs.ktor.cio)
  implementation(libs.ktor.ccn)
  implementation(libs.ktor.json)
}