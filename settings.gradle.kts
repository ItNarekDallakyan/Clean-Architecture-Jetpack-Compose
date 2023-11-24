pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ithd-editor-app"
include(":editor-app")
include(":core")
include(":core:data")
include(":core:database")
include(":core:network")
include(":core:ui")
include(":feature")
include(":core:datastore")
include(":core:domain")
include(":feature:browse")
include(":feature:settings")
include(":feature:editor")
include(":core:common")
include(":core:model")
