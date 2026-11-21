plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "com.xiaomi.miwear.mishow"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.xiaomi.miwear.mishow"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}
