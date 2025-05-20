package com.radusalagean.uitextcompose.core

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

@InternalApi
public object UITextUtil {
    // Regex to match both numbered (%1$s) and unnumbered (%s) placeholders,
    //  excluding escaped ones
    private val placeholderRegexAndroidStringResources: Regex =
        Regex("(?<!%)%(\\d+\\$)?[s]")

    // Regex to match only numbered (%1$s) placeholders
    private val placeholderRegexComposeMultiplatformStringResources: Regex =
        Regex("%(\\d+\\$)[s]")

    // Regex to match escaped placeholders (%%1$s or %%s)
    private val escapedPlaceholderRegex: Regex = Regex("%%([0-9]+\\$)?s")

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
     *
     * For more details, see the [API documentation](https://developer.android.com/guide/topics/resources/string-resource).
     */
    public fun buildAnnotatedStringWithAndroidStringResourceRules(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseStringProvider: () -> String
    ): CharSequence {
        return buildAnnotatedStringInternal(
            resolvedArgs = resolvedArgs,
            baseAnnotations = baseAnnotations,
            baseString = baseStringProvider(),
            placeholderRegex = placeholderRegexAndroidStringResources,
            removeEscapedPlaceholderCharacter = true
        )
    }

    /**
     * Builds an annotated string using a suspend string provider
     * Used by the implementation for Compose Multiplatform String Resources
     *
     * For more details, see the [API documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-usage.html#strings).
     */
    public suspend fun buildAnnotatedStringWithComposeMultiplatformStringResourceRules(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseStringProvider: suspend () -> String
    ): CharSequence {
        return buildAnnotatedStringInternal(
            resolvedArgs = resolvedArgs,
            baseAnnotations = baseAnnotations,
            baseString = baseStringProvider(),
            placeholderRegex = placeholderRegexComposeMultiplatformStringResources,
            removeEscapedPlaceholderCharacter = false
        )
    }

    /**
     * Internal implementation that both sync and suspend functions delegate to
     */
    private fun buildAnnotatedStringInternal(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseString: String,
        placeholderRegex: Regex,
        removeEscapedPlaceholderCharacter: Boolean
    ): CharSequence {
        return androidx.compose.ui.text.buildAnnotatedString {
            var unnumberedCount = 0
            val placeholders = placeholderRegex.findAll(baseString).map {
                val group = it.groups[1]?.value
                if (group != null) {
                    // For numbered placeholders (%1$s), extract the number
                    group.removeSuffix("$").toInt() - 1
                } else {
                    // For unnumbered placeholders (%s), use sequential count
                    unnumberedCount++
                    unnumberedCount - 1
                }
            }.toList()

            var parts = baseString.split(placeholderRegex)
            if (removeEscapedPlaceholderCharacter) {
                // Remove leading % from escaped placeholders in each part
                parts = parts.map { part ->
                    part.replace(escapedPlaceholderRegex) { matchResult ->
                        matchResult.value.substring(1)
                    }
                }
            }
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