package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import kotlin.jvm.JvmInline

/**
 * Represents text annotations for styling and linking.
 * 
 * This sealed interface provides a type-safe way to represent different kinds of
 * text annotations used for styling and formatting text in Compose UI.
 * 
 * The library uses these annotations internally to apply styling to text. Users
 * typically add annotations through the DSL rather than creating instances directly.
 * 
 * @see Span
 * @see Paragraph
 * @see Link
 */
@InternalApi
public sealed interface UITextAnnotation {

    /**
     * Represents character-level styling for text.
     * 
     * Span styles apply to individual characters and can include properties like
     * color, font weight, font style, letter spacing, etc.
     * 
     * @property spanStyle The Compose UI [SpanStyle] to apply to the text.
     */
    @JvmInline
    public value class Span(
        internal val spanStyle: SpanStyle
    ) : UITextAnnotation

    /**
     * Represents paragraph-level styling for text.
     * 
     * Paragraph styles apply to entire paragraphs and can include properties like
     * alignment, indentation, line height, etc.
     * 
     * @property paragraphStyle The Compose UI [ParagraphStyle] to apply to the text.
     */
    @JvmInline
    public value class Paragraph(
        internal val paragraphStyle: ParagraphStyle
    ) : UITextAnnotation

    /**
     * Represents a link annotation for text.
     * 
     * Link annotations can be used to make portions of text clickable and associate
     * metadata like URLs with them.
     * 
     * @property linkAnnotation The Compose UI [LinkAnnotation] to apply to the text.
     */
    @JvmInline
    public value class Link(
        internal val linkAnnotation: LinkAnnotation
    ) : UITextAnnotation
} 