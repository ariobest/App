plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    buildFeatures { buildConfig = false }
    namespace="com.ario.filemanager"
    compileSdk=35
    defaultConfig {
        applicationId="com.ario.filemanager"
        minSdk=26
        targetSdk=35
        versionCode=1
        versionName="1.0"
    }
}
