import com.radusalagean.uitextcompose.Config
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.binaryCompatibilityValidator)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = Config.artifactGroup
version = Config.versionName

kotlin {
    jvm("desktop")
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":uitextcompose-core"))
                api(compose.runtime)
                api(compose.ui)
                api(compose.components.resources)
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(compose.desktop.currentOs)
                implementation(compose.desktop.uiTestJUnit4)
            }
        }
    }
    explicitApi()
}

android {
    namespace = "${Config.rootPackage}.multiplatform"
    compileSdk = Config.compileSdk
    defaultConfig {
        minSdk = Config.minSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "ui-text-compose-multiplatform", version.toString())
}
