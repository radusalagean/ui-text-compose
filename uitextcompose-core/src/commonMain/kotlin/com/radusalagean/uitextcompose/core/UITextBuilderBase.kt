package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle

@InternalApi
@DslMarker
public annotation class UITextDslMarker

@OptIn(InternalApi::class)
@UITextDslMarker
public interface UITextBuilderBase<T> {
    public fun raw(text: CharSequence)
    public fun build(): T
}

@OptIn(InternalApi::class)
@UITextDslMarker
public class ResBuilder : AnnotationsBuilder() {
    private val args = mutableListOf<Pair<Any, List<UITextAnnotation>>>()

    public fun arg(
        value: CharSequence,
        annotationsBuilder: AnnotationsBuilder.() -> Unit = { }
    ) {
        val builder = AnnotationsBuilder().apply(annotationsBuilder)
        args += value to builder.buildAnnotations()
    }

    public fun <T : UITextBase> arg(
        value: T,
        annotationsBuilder: AnnotationsBuilder.() -> Unit = { }
    ) {
        val builder = AnnotationsBuilder().apply(annotationsBuilder)
        args += value to builder.buildAnnotations()
    }

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

@OptIn(InternalApi::class)
@UITextDslMarker
public open class AnnotationsBuilder {
    internal val annotations: MutableList<UITextAnnotation> = mutableListOf()

    public operator fun SpanStyle.unaryPlus() {
        annotations.add(UITextAnnotation.Span(this))
    }

    public operator fun ParagraphStyle.unaryPlus() {
        annotations.add(UITextAnnotation.Paragraph(this))
    }

    public operator fun LinkAnnotation.unaryPlus() {
        annotations.add(UITextAnnotation.Link(this))
    }

    public fun buildAnnotations(): List<UITextAnnotation> = annotations
}