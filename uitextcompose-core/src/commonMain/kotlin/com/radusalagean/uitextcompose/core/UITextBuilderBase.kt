package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle

@InternalApi
@DslMarker
public annotation class UITextDslMarker

/**
 * Base interface for building text representations in a type-safe DSL.
 * 
 * This interface provides a foundation for DSL builders that create text representations
 * for Compose UI. Implementations can add text from various sources and build
 * the final result with appropriate styling.
 * 
 * @param T The type of the result built by this builder.
 * 
 * @see raw
 * @see build
 */
@OptIn(InternalApi::class)
@UITextDslMarker
public interface UITextBuilderBase<T> {
    /**
     * Adds raw text content to the builder.
     * 
     * Use this method to add plain text that doesn't come from a resource.
     * 
     * Example:
     * ```
     * UITextBuilder().apply {
     *     raw("Hello, World!")
     * }.build()
     * ```
     * 
     * @param text The text content to add.
     */
    public fun raw(text: CharSequence)
    
    /**
     * Builds and returns the final text representation.
     * 
     * Call this method after adding all the needed content to get the resulting text object.
     * 
     * @return The built text representation of type [T].
     */
    public fun build(): T
}

/**
 * Builder for configuring string resource arguments and annotations.
 * 
 * This builder is used to add arguments to string resources and apply text styling.
 * It supports both simple text arguments and nested [UITextBase] objects as arguments.
 * 
 * Example:
 * ```
 * UITextBuilder().apply {
 *     res(R.string.greeting) {
 *         arg("World") {
 *             +SpanStyle(color = Color.Blue)
 *         }
 *     }
 * }.build()
 * ```
 */
@OptIn(InternalApi::class)
@UITextDslMarker
public class ResBuilder : AnnotationsBuilder() {
    private val args = mutableListOf<Pair<Any, List<UITextAnnotation>>>()

    /**
     * Adds a text argument to the string resource.
     * 
     * Use this method to provide arguments for string resource placeholders.
     * You can optionally apply styling to the argument.
     * 
     * @param value The text value to use as an argument.
     * @param annotationsBuilder Optional lambda to apply styling to this argument.
     */
    public fun arg(
        value: CharSequence,
        annotationsBuilder: AnnotationsBuilder.() -> Unit = { }
    ) {
        val builder = AnnotationsBuilder().apply(annotationsBuilder)
        args += value to builder.buildAnnotations()
    }

    /**
     * Adds a [UITextBase] argument to the string resource.
     * 
     * Use this method to provide nested text objects as arguments for string resource placeholders.
     * You can optionally apply additional styling to the argument.
     * 
     * @param value The [UITextBase] object to use as an argument.
     * @param annotationsBuilder Optional lambda to apply styling to this argument.
     */
    public fun <T : UITextBase> arg(
        value: T,
        annotationsBuilder: AnnotationsBuilder.() -> Unit = { }
    ) {
        val builder = AnnotationsBuilder().apply(annotationsBuilder)
        args += value to builder.buildAnnotations()
    }

    /**
     * Builds the resource configuration with all arguments and annotations.
     * 
     * This is called internally by the library to create the configuration needed
     * to build the final text representation.
     * 
     * @return A [ResConfig] object containing all arguments and annotations.
     */
    public fun buildResConfig(): ResConfig = ResConfig(
        annotations = annotations,
        args = args
    )
}

@InternalApi
public class ResConfig(
    public val annotations: List<UITextAnnotation>,
    public val args: List<Pair<Any, List<UITextAnnotation>>>
)

/**
 * Builder for text annotations like styling and links.
 * 
 * This builder allows adding styling information to text using a DSL syntax.
 * It supports span styles, paragraph styles, and link annotations.
 * 
 * Example:
 * ```
 * AnnotationsBuilder().apply {
 *     +SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)
 *     +ParagraphStyle(textAlign = TextAlign.Center)
 *     +LinkAnnotation.Url(url = "https://example.com")
 * }
 * ```
 */
@OptIn(InternalApi::class)
@UITextDslMarker
public open class AnnotationsBuilder {
    internal val annotations: MutableList<UITextAnnotation> = mutableListOf()

    /**
     * Adds a span style to the text.
     * 
     * Use the unary plus operator to add styling like color, font weight, or text decoration.
     * 
     * Example:
     * ```
     * +SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)
     * ```
     * 
     * @param SpanStyle The span style to add.
     */
    public operator fun SpanStyle.unaryPlus() {
        annotations.add(UITextAnnotation.Span(this))
    }

    /**
     * Adds a paragraph style to the text.
     * 
     * Use the unary plus operator to add paragraph styling like alignment or indentation.
     * 
     * Example:
     * ```
     * +ParagraphStyle(textAlign = TextAlign.Center)
     * ```
     * 
     * @param ParagraphStyle The paragraph style to add.
     */
    public operator fun ParagraphStyle.unaryPlus() {
        annotations.add(UITextAnnotation.Paragraph(this))
    }

    /**
     * Adds a link annotation to the text.
     * 
     * Use the unary plus operator to add a clickable link to the text.
     * 
     * Example:
     * ```
     * +LinkAnnotation.Url(url = "https://example.com")
     * ```
     * 
     * @param LinkAnnotation The link annotation to add.
     */
    public operator fun LinkAnnotation.unaryPlus() {
        annotations.add(UITextAnnotation.Link(this))
    }

    /**
     * Builds and returns all annotations added to this builder.
     * 
     * This is called internally by the library to retrieve the annotations.
     * 
     * @return A list of [UITextAnnotation] objects representing the added annotations.
     */
    public fun buildAnnotations(): List<UITextAnnotation> = annotations
}