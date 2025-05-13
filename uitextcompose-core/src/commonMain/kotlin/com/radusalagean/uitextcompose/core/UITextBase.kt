package com.radusalagean.uitextcompose.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString

public interface UITextBase {
    @Composable
    public fun buildStringComposable(): String

    @Composable
    public fun buildAnnotatedStringComposable(): AnnotatedString
}
