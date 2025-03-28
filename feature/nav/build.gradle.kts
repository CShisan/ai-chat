plugins {
  id("cshisan.android.library")
  id("cshisan.android.library.compose")
  id("cshisan.android.feature")
  id("cshisan.android.hilt")
  id("cshisan.spotless")
}

android {
  namespace = "com.cshisan.feature.nav"
}

dependencies {
  api(projects.feature.chat)
  api(projects.feature.login)
  api(projects.feature.setting)

  implementation(libs.androidx.material3.android)
}