package com.radusalagean.uitextcompose.multiplatform.sample.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LanguageManagerAndroid : LanguageManager {
    override fun getCurrentLanguageCode(): String {
        return extractLanguageCode()
    }

    override fun onLanguageSelected(code: String) {
        val localesList = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(localesList)
    }

    private fun extractLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return locales.get(0)?.language ?: "en"
    }
}