package com.radusalagean.uitextcompose.kmp.sample.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.window

class LanguageManagerWasmJs : LanguageManager {

    var currentLanguage: String by mutableStateOf(getBrowserLanguage())
        private set

    override fun getCurrentLanguageCode(): String {
        return currentLanguage
    }

    override fun onLanguageSelected(code: String) {
        // No-op
    }

    private fun getBrowserLanguage(): String {
        return window.navigator.language.substringBefore("-")
    }
}