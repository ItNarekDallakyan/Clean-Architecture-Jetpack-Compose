plugins {
    id("ithd.android.library")
}

android {
    namespace = "org.ithd.editor.core.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.android)
}