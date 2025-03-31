plugins {
  id("cshisan.android.library")
  id("cshisan.spotless")
}

android {
  namespace = "com.cshisan.core.common"
}

dependencies {
  implementation(libs.gson)
}