pluginManagement {
    repositories {
        google()  // Ensures access to Google services plugins
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.google.gms.google-services") version "4.3.15"
        id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" // Add the google-services plugin with version
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // Required for Firebase and Google services
        mavenCentral()
    }
}

rootProject.name = "SmartCard"
include(":app")
