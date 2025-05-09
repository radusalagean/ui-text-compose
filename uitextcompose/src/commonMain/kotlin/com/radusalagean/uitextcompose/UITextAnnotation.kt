package com.radusalagean.uitextcompose

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import kotlin.jvm.JvmInline

sealed interface UITextAnnotation {

    @JvmInline
    value class Span(
        val spanStyle: SpanStyle
    ) : UITextAnnotation

    @JvmInline
    value class Paragraph(
        val paragraphStyle: ParagraphStyle
    ) : UITextAnnotation

    @JvmInline
    value class Link(
        val linkAnnotation: LinkAnnotation
    ) : UITextAnnotation
}
