plugins {
    id("ithd.android.library")
}

android {
    namespace = "org.ithd.editor.core.model"
}

dependencies {
    implementation(libs.kotlinx.datetime)
}