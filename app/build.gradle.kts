plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.sysopbp.rootforgesu"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.sysopbp.rootforgesu"
        minSdk = 29
        targetSdk = 35
        versionCode = 5
        versionName = "0.5.0-alpha"
    }
    externalNativeBuild {
        cmake { path = file("src/main/cpp/CMakeLists.txt") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
