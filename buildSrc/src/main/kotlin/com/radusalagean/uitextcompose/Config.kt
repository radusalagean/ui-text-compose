package com.radusalagean.uitextcompose

object Config {
    const val minSdk = 21
    const val targetSdk = 35
    const val compileSdk = 35
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 0
    const val releaseCandidate = 0
    val versionName = "$majorVersion.$minorVersion.$patchVersion${if (releaseCandidate > 0) "-rc$releaseCandidate" else ""}"
    const val versionCode = 1
    const val artifactGroup = "com.radusalagean"
    const val rootPackage = "$artifactGroup.uitextcompose"
}