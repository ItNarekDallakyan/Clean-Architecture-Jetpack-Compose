import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidMedia3ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("ithd.android.library")
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                add("implementation", libs.findLibrary("androidx.media3.exoplayer").get())
                add("implementation", libs.findLibrary("androidx.media3.exoplayer.dash").get())
                add("implementation", libs.findLibrary("androidx.media3.ui").get())
                add("implementation", libs.findLibrary("androidx.media3.cast").get())
            }
        }
    }
}