package com.radusalagean.uitextcompose.multiplatform.sample.util

interface LanguageManager {
    fun getCurrentLanguageCode(): String
    fun onLanguageSelected(code: String)
}