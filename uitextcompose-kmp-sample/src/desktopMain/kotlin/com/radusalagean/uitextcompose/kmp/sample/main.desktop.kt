package com.radusalagean.uitextcompose.kmp.sample

import androidx.compose.runtime.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.radusalagean.uitextcompose.kmp.sample.ui.screen.MainScreen
import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManager
import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManagerDesktop
import org.koin.compose.koinInject

fun main() = application {
    application()
    val languageManager: LanguageManagerDesktop =
        koinInject<LanguageManager>() as LanguageManagerDesktop

    Window(
        onCloseRequest = ::exitApplication,
        title = "UIText Compose KMP Sample",
    ) {
        key(languageManager.currentLanguage) {
            MainScreen()
        }
    }
}