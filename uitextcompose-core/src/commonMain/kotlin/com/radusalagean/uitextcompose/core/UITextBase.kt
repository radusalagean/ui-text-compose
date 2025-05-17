package com.radusalagean.uitextcompose.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString

/**
 * Base interface for text representation in Compose UI.
 * 
 * This interface defines the core functionality for text that can be rendered in Compose UI.
 * Implementations can retrieve text from various sources (raw strings, resource files, etc.)
 * and provide both plain text and annotated text output in a composable context.
 * 
 * Example usage:
 * ```
 * val myText: UITextBase = // implementation of UITextBase
 * 
 * @Composable
 * fun DisplayText(text: UITextBase) {
 *     // Get a plain string
 *     Text(text = text.buildStringComposable())
 *     
 *     // Or get an annotated string with styling
 *     Text(text = text.buildAnnotatedStringComposable())
 * }
 * ```
 * 
 * @see buildStringComposable
 * @see buildAnnotatedStringComposable
 */
public interface UITextBase {
    /**
     * Builds and returns a plain text string in a composable context.
     * 
     * This function resolves the text content from its source (e.g., string resources, raw text) 
     * and returns it as a plain string. Any styling information will be lost.
     * 
     * @return A plain string representation of the text content.
     */
    @Composable
    public fun buildStringComposable(): String

    /**
     * Builds and returns an annotated string with styling in a composable context.
     * 
     * This function resolves the text content from its source (e.g., string resources, raw text)
     * and returns it as an [AnnotatedString] that preserves styling information like spans,
     * paragraph styles, and link annotations.
     * 
     * @return An [AnnotatedString] representation of the text content with styling information.
     */
    @Composable
    public fun buildAnnotatedStringComposable(): AnnotatedString
}
