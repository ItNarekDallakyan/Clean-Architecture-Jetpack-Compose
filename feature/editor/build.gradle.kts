plugins {
    id("ithd.android.feature")
    id("ithd.android.media3")
    id("ithd.android.library.compose")
}

android {
    namespace = "org.ithd.editor.feature.editor"
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.activity.compose)
}