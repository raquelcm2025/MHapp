pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
    plugins {
        id("com.android.application") version "8.13.0" apply false
        id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()











































    }
}

rootProject.name = "MyHobbiesApp"
include(":app")
 