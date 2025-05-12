package com.radusalagean.uitextcompose.kmp.sample

import androidx.compose.runtime.key
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.radusalagean.uitextcompose.kmp.sample.ui.screen.MainScreen
import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManager
import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManagerWasmJs
import kotlinx.browser.document
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application()

    ComposeViewport(document.body!!) {
        val languageManager: LanguageManagerWasmJs =
            koinInject<LanguageManager>() as LanguageManagerWasmJs

        key(languageManager.currentLanguage) {
            MainScreen(
                languagePickerEnabled = false
            )
        }
    }
}