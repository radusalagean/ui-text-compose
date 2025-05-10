package com.radusalagean.uitextcompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

sealed class UIText {

    protected abstract suspend fun build(): CharSequence

    @Composable
    private fun <T> rememberResourceState(
        getDefault: () -> T,
        block: suspend () -> T
    ): State<T> {
        val scope = rememberCoroutineScope()
        return remember {
            val mutableState = mutableStateOf(getDefault())
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                mutableState.value = block()
            }
            mutableState
        }
    }

    suspend fun buildString(): String {
        return when (val charSequence = build()) {
            is String -> charSequence
            is AnnotatedString -> charSequence.toString() // We drop any style here
            else -> ""
        }
    }

    @Composable
    fun buildStringComposable(): String { // TODO Rename
        val string by rememberResourceState({ "" }) {
            buildString()
        }
        return string
    }

    suspend fun buildAnnotatedString(): AnnotatedString {
        return when (val charSequence = build()) {
            is String -> AnnotatedString(charSequence)
            is AnnotatedString -> charSequence
            else -> AnnotatedString("")
        }
    }

    @Composable
    fun buildAnnotatedStringComposable(): AnnotatedString { // TODO Rename
        val annotatedString by rememberResourceState({ AnnotatedString("") }) {
            buildAnnotatedString()
        }
        return annotatedString
    }

    private fun AnnotatedString.Builder.appendAny(arg: Any) {
        when (arg) {
            is CharSequence -> append(arg)
            else -> append(arg.toString())
        }
    }

    protected suspend fun resolveArg(arg: Any) = when (arg) {
        is UIText -> arg.build()
        else -> arg
    }

    /**
     * In order to work around the default behavior of Android's getString(...) which will
     *  drop any associated style, we generate placeholders in the form of ${0}, ${1}, etc
     *  which will allow us to apply our custom logic to inject the arguments without losing
     *  associated styles, in the correct order.
     *
     * Warning: We use ${digit} placeholders, so make sure you don't have such patterns hardcoded in
     *  your string resources. Also, make sure you exclusively use "%s" (no order needed) or
     *  "%1$s", "%2$s", etc. (specified order) in string files for arguments.
     *
     * Other formats in string resources "%.2f", "%d", etc. are not supported, but you can safely
     *  replace them with string format arguments (e.g. %s) and offload formatting of other types
     *  to your kotlin code.
     */
    private fun generatePlaceholderArgs(placeholdersCount: Int) =
        List(placeholdersCount) { "\${$it}" }.toTypedArray()


    protected suspend fun getStringWithPlaceholders(
        stringResource: StringResource,
        placeholdersCount: Int
    ): String = getString(
        stringResource,
        *generatePlaceholderArgs(placeholdersCount)
    )

    protected suspend fun getQuantityStringWithPlaceholders(
        pluralStringResource: PluralStringResource,
        quantity: Int,
        placeholdersCount: Int
    ): String = getPluralString(
        pluralStringResource,
        quantity,
        *generatePlaceholderArgs(placeholdersCount)
    )

    protected suspend fun buildAnnotatedString(
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>,
        baseStringProvider: suspend () -> String
    ): CharSequence {
        return buildAnnotatedString {
            val baseString = baseStringProvider()
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
                else -> { }
            }
        }

        applyAnnotation(0)
    }

    class Raw(val text: CharSequence) : UIText() {

        override suspend fun build(): CharSequence {
            return text
        }
    }

    class Res(
        val stringResource: StringResource,
        val args: List<Pair<Any, List<UITextAnnotation>>>,
        val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                buildAnnotatedString(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        getStringWithPlaceholders(
                            stringResource = stringResource,
                            placeholdersCount = args.size
                        )
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                getString(stringResource, *resolvedArgs.map { it.first }.toTypedArray())
            } else {
                getString(stringResource)
            }
        }
    }

    class PluralRes(
        val pluralStringResource: PluralStringResource,
        val quantity: Int,
        val args: List<Pair<Any, List<UITextAnnotation>>>,
        val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                buildAnnotatedString(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        getQuantityStringWithPlaceholders(
                            pluralStringResource = pluralStringResource,
                            quantity = quantity,
                            placeholdersCount = args.size
                        )
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                getPluralString(pluralStringResource, quantity,
                    *resolvedArgs.map { it.first }.toTypedArray())
            } else {
                getPluralString(pluralStringResource, quantity)
            }
        }
    }

    class Compound(
        val components: List<UIText>
    ) : UIText() {

        private fun concat(parts: List<CharSequence>): CharSequence {
            if (parts.isEmpty())
                return ""

            if (parts.size == 1)
                return parts[0]

            val annotated = parts.any { it is AnnotatedString }
            return if (annotated) {
                buildAnnotatedString {
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

        override suspend fun build(): CharSequence {
            val resolvedComponents = components.map { it.build() }
            return concat(resolvedComponents)
        }
    }

    companion object {
        private val placeholderRegex = Regex("\\$\\{(\\d+)\\}")
    }
}