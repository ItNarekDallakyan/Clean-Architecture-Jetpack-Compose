plugins {
    id("ithd.android.library")
    id("ithd.android.hilt")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "org.ithd.editor.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
}