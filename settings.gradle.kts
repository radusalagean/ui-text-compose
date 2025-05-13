rootProject.name = "ui-text-compose"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(":uitextcompose-core")
include(":uitextcompose-kmp")
include(":uitextcompose-kmp-sample")
include(":uitextcompose-android")
include(":uitextcompose-android-sample")
