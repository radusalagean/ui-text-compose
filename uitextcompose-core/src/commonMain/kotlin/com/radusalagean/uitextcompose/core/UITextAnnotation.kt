package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import kotlin.jvm.JvmInline

@InternalApi
public sealed interface UITextAnnotation {

    @JvmInline
    public value class Span(
        internal val spanStyle: SpanStyle
    ) : UITextAnnotation

    @JvmInline
    public value class Paragraph(
        internal val paragraphStyle: ParagraphStyle
    ) : UITextAnnotation

    @JvmInline
    public value class Link(
        internal val linkAnnotation: LinkAnnotation
    ) : UITextAnnotation
} 