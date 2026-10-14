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
        versionCode = 2
        versionName = "0.2.0-alpha"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
