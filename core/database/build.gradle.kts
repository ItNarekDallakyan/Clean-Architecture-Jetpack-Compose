plugins {
    id("ithd.android.library")
    id("ithd.android.hilt")
    id("ithd.android.room")
}

android {
    namespace = "org.ithd.editor.core.database"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
}