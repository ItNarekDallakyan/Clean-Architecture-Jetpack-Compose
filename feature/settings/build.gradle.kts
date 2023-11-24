plugins {
    id("ithd.android.feature")
    id("ithd.android.library.compose")
}

android {
    namespace = "org.ithd.editor.feature.settings"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.activity.compose)
}