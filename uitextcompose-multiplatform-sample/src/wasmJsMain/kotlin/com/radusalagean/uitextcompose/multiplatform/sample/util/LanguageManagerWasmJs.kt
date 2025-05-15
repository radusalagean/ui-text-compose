package com.radusalagean.uitextcompose.multiplatform.sample.util

import kotlinx.browser.window

class LanguageManagerWasmJs : LanguageManager {
    override fun getCurrentLanguageCode(): String {
        return getBrowserLanguage()
    }

    override fun onLanguageSelected(code: String) {
        // No-op
    }

    private fun getBrowserLanguage(): String {
        return window.navigator.language.substringBefore("-")
    }
}