package com.radusalagean.uitextcompose.multiplatform.sample

import androidx.compose.runtime.key
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.radusalagean.uitextcompose.multiplatform.sample.ui.screen.MainScreen
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManager
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManagerDesktop
import org.koin.compose.koinInject

fun main() = application {
    application()
    val languageManager: LanguageManagerDesktop =
        koinInject<LanguageManager>() as LanguageManagerDesktop

    Window(
        onCloseRequest = ::exitApplication,
        title = "UIText Compose Multiplatform Sample",
    ) {
        key(languageManager.currentLanguage) {
            MainScreen()
        }
    }
}