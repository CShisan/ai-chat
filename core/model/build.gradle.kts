plugins {
  id("cshisan.android.library")
  id("cshisan.android.library.compose")
  id("cshisan.spotless")
}

android {
  namespace = "com.cshisan.core.model"
}

dependencies {
  implementation(libs.androidx.compose.runtime)
}