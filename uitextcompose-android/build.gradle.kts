import com.radusalagean.uitextcompose.Config
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = Config.artifactGroup
version = Config.versionName

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(project(":uitextcompose-core"))
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.ui)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
                implementation(libs.kotlin.test)
                implementation(libs.ext.junit)
                implementation(libs.espresso.core)
                implementation(libs.androidx.compose.ui.test.junit4)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
    explicitApi()
}

android {
    namespace = "${Config.rootPackage}.android"
    compileSdk = Config.compileSdk
    defaultConfig {
        minSdk = Config.minSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "ui-text-compose-android", version.toString())
}
