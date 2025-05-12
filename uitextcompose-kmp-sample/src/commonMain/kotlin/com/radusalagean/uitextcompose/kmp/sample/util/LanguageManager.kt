package com.radusalagean.uitextcompose.kmp.sample.util

interface LanguageManager {
    fun getCurrentLanguageCode(): String
    fun onLanguageSelected(code: String)
}