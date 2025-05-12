package com.radusalagean.uitextcompose.kmp.sample.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LanguageManagerAndroid : LanguageManager {
    override fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return locales.get(0)?.language ?: "en"
    }

    override fun onLanguageSelected(code: String) {
        val localesList = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(localesList)
    }
}