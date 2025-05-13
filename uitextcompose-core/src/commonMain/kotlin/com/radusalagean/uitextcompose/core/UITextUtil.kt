package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

public object UITextUtil {
    private val placeholderRegex: Regex = Regex("\\$\\{(\\d+)\\}")

    public fun generatePlaceholderArgs(placeholdersCount: Int): Array<String> =
        List(placeholdersCount) { "\${$it}" }.toTypedArray()

    private fun AnnotatedString.Builder.appendAny(arg: Any) {
        when (arg) {
            is CharSequence -> append(arg)
            else -> append(arg.toString())
        }
    }

    private fun AnnotatedString.Builder.handleUITextAnnotations(
        uiTextAnnotations: List<UITextAnnotation>,
        block: () -> Unit
    ) {
        if (uiTextAnnotations.isEmpty()) {
            block()
            return
        }

        fun applyAnnotation(index: Int) {
            if (index >= uiTextAnnotations.size) {
                block()
                return
            }

            when (val annotation = uiTextAnnotations[index]) {
                is UITextAnnotation.Span -> {
                    withStyle(annotation.spanStyle) {
                        applyAnnotation(index + 1)
                    }
                }
                is UITextAnnotation.Paragraph -> {
                    withStyle(annotation.paragraphStyle) {
                        applyAnnotation(index + 1)
                    }
                }
                is UITextAnnotation.Link -> {
                    withLink(annotation.linkAnnotation) {
                        applyAnnotation(index + 1)
                    }
                }
            }
        }

        applyAnnotation(0)
    }

    /**
     * Builds an annotated string using a synchronous string provider
     * Used by the implementation for Android String Resources
     */
    public fun buildAnnotatedString(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseStringProvider: () -> String
    ): CharSequence {
        return buildAnnotatedStringInternal(
            resolvedArgs = resolvedArgs,
            baseAnnotations = baseAnnotations,
            baseString = baseStringProvider()
        )
    }

    /**
     * Builds an annotated string using a suspend string provider
     * Used by the implementation for Compose Multiplatform Resources
     */
    public suspend fun buildAnnotatedStringSuspend(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseStringProvider: suspend () -> String
    ): CharSequence {
        return buildAnnotatedStringInternal(
            resolvedArgs = resolvedArgs,
            baseAnnotations = baseAnnotations,
            baseString = baseStringProvider()
        )
    }

    /**
     * Internal implementation that both sync and suspend functions delegate to
     */
    private fun buildAnnotatedStringInternal(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseString: String
    ): CharSequence {
        return androidx.compose.ui.text.buildAnnotatedString {
            val parts = baseString.split(placeholderRegex)
            val placeholders = placeholderRegex.findAll(baseString).map {
                it.groups[1]!!.value.toInt()
            }.toList()

            handleUITextAnnotations(baseAnnotations) {
                parts.forEachIndexed { index, part ->
                    append(part)
                    if (index !in placeholders.indices)
                        return@handleUITextAnnotations
                    val placeholderIndex = placeholders[index]
                    val uiTextAnnotations = resolvedArgs[placeholderIndex].second
                    val arg = resolvedArgs[placeholderIndex].first
                    handleUITextAnnotations(uiTextAnnotations) {
                        appendAny(arg)
                    }
                }
            }
        }
    }

    /**
     * Utility to concatenate CharSequences with style preservation
     */
    public fun concat(parts: List<CharSequence>): CharSequence {
        if (parts.isEmpty())
            return ""

        if (parts.size == 1)
            return parts[0]

        val annotated = parts.any { it is AnnotatedString }
        return if (annotated) {
            androidx.compose.ui.text.buildAnnotatedString {
                parts.forEach {
                    append(it)
                }
            }
        } else {
            buildString {
                parts.forEach {
                    append(it)
                }
            }
        }
    }
}