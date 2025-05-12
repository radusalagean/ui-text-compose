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
include(":uitextcompose-android")
include(":uitextcompose-kmp")
include(":uitextcompose-sample")
