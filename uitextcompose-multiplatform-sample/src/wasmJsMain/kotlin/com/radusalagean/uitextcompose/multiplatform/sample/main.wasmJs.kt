package com.radusalagean.uitextcompose.multiplatform.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.radusalagean.uitextcompose.multiplatform.sample.ui.screen.MainScreen
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    application()

    ComposeViewport(document.body!!) {
        MainScreen(
            languagePickerEnabled = false
        )
    }
}