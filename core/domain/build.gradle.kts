plugins {
    id("ithd.android.library")
    kotlin("kapt")
}

android {
    namespace = "org.ithd.editor.core.domain"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    kapt(libs.hilt.compiler)
}