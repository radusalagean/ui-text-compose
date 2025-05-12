package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.AnnotatedString

public interface UITextBase<T> {
    public fun buildString(context: T): String
    public fun buildAnnotatedString(context: T): AnnotatedString
}
