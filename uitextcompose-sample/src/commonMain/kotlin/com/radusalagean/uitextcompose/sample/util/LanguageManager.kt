package com.radusalagean.uitextcompose.sample.util

interface LanguageManager {
    fun getCurrentLanguageCode(): String
    fun onLanguageSelected(code: String)
}