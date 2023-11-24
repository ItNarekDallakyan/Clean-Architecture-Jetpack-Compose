plugins {
    id("ithd.android.library")
    id("ithd.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.ithd.editor.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}