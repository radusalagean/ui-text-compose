package com.radusalagean.uitextcompose

import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

@DslMarker
internal annotation class UITextDslMarker

public fun UIText(block: UITextBuilder.() -> Unit): UIText = UITextBuilder().apply(block).build()

@UITextDslMarker
public class UITextBuilder {
    private val components = mutableListOf<UIText>()

    public fun raw(text: CharSequence) {
        components += UIText.Raw(text)
    }

    public fun res(
        stringResource: StringResource,
        resBuilder: ResBuilder.() -> Unit = { }
    ) {
        val config = ResBuilder().apply(resBuilder).buildResConfig()
        components += UIText.Res(
            stringResource = stringResource,
            args = config.args,
            baseAnnotations = config.annotations
        )
    }

    public fun pluralRes(
        pluralStringResource: PluralStringResource,
        quantity: Int,
        resBuilder: ResBuilder.() -> Unit = {
            arg(quantity.toString())
        }
    ) {
        val config = ResBuilder().apply(resBuilder).buildResConfig()
        components += UIText.PluralRes(
            pluralStringResource = pluralStringResource,
            quantity = quantity,
            args = config.args,
            baseAnnotations = config.annotations
        )
    }

    internal fun build(): UIText = when (components.size) {
        0 -> UIText.Raw("")
        1 -> components[0]
        else -> UIText.Compound(components)
    }
}

@UITextDslMarker
public class ResBuilder : AnnotationsBuilder() {
    private val args = mutableListOf<Pair<Any, List<UITextAnnotation>>>()

    public fun arg(value: CharSequence, annotationsBuilder: AnnotationsBuilder.() -> Unit = { }) {
        val annotations = AnnotationsBuilder().apply(annotationsBuilder).buildAnnotations()
        args += value to annotations
    }

    public fun arg(value: UIText, annotationsBuilder: AnnotationsBuilder.() -> Unit = { }) {
        val annotations = AnnotationsBuilder().apply(annotationsBuilder).buildAnnotations()
        args += value to annotations
    }

    public fun buildResConfig(): ResAnnotatedConfig = ResAnnotatedConfig(
        annotations = annotations,
        args = args
    )
}

public class ResAnnotatedConfig(
    internal val annotations: List<UITextAnnotation>,
    internal val args: List<Pair<Any, List<UITextAnnotation>>>
)

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