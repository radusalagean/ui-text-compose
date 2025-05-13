import com.radusalagean.uitextcompose.Config
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = Config.artifactGroup
version = Config.versionName

kotlin {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":uitextcompose-core"))
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "ui-text-compose-android", version.toString())
} 