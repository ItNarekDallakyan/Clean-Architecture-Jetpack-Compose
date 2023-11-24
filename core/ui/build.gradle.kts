plugins {
    id("ithd.android.library")
    id("ithd.android.library.compose")
    id("kotlinx-serialization")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "org.ithd.editor.core.core.ui"
}

dependencies {
    implementation(project(":core:model"))

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.runtime.livedata)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.ui.util)
//    api(libs.androidx.metrics)
//    api(libs.androidx.tracing.ktx)

    debugApi(libs.androidx.compose.ui.tooling)

    //  implementation(libs.androidx.browser)
    implementation(libs.androidx.core.ktx)
//    implementation(libs.coil.kt)
//    implementation(libs.coil.kt.compose)
    implementation(libs.kotlinx.datetime)
    implementation(libs.retrofit.kotlin.serialization)
}