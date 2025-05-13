package com.radusalagean.uitextcompose.multiplatform.sample

import androidx.compose.runtime.key
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.radusalagean.uitextcompose.multiplatform.sample.ui.screen.MainScreen
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManager
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManagerWasmJs
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