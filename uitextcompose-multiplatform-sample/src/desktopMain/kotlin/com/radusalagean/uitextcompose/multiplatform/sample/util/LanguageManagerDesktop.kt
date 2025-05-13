package com.radusalagean.uitextcompose.multiplatform.sample.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

class LanguageManagerDesktop : LanguageManager {

    var currentLanguage: String by mutableStateOf(Locale.getDefault().language)
        private set

    override fun getCurrentLanguageCode(): String {
        return currentLanguage
    }

    override fun onLanguageSelected(code: String) {
        Locale.setDefault(Locale.of(code))
        currentLanguage = code
    }
}