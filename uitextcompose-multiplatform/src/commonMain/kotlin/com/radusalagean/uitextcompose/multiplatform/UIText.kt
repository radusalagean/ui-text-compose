package com.radusalagean.uitextcompose.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.AnnotatedString
import com.radusalagean.uitextcompose.core.InternalApi
import com.radusalagean.uitextcompose.core.UITextAnnotation
import com.radusalagean.uitextcompose.core.UITextBase
import com.radusalagean.uitextcompose.core.UITextUtil
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.rememberResourceEnvironment

/**
 * Implementation of [UITextBase] for handling text in Compose Multiplatform applications
 *  using multiplatform string resources.
 * 
 * This class provides a way to work with text from various sources such as:
 * - Raw text strings
 * - Compose Multiplatform string resources
 * - Compose Multiplatform plural resources
 * 
 * It handles string formatting with placeholders and supports styling through span styles,
 * paragraph styles, and link annotations. This implementation works across all platforms
 * supported by Compose Multiplatform.
 * 
 * Example usage:
 * ```
 * // Create a UIText instance using the DSL
 * val text = UIText {
 *     res(Res.string.greeting) {
 *         arg("User") {
 *             +SpanStyle(color = Color.Blue) 
 *         }
 *     }
 * }
 * 
 * // Use it in a Composable
 * @Composable
 * fun Greeting(text: UIText) {
 *     Text(text = text.buildAnnotatedStringComposable())
 * }
 * ```
 * 
 * @see UITextBuilder
 * @see UITextBase
 */
@OptIn(InternalApi::class)
public sealed class UIText : UITextBase {

    protected abstract suspend fun build(): CharSequence

    @Composable
    private fun <T> rememberResourceState(
        getDefault: () -> T,
        block: suspend () -> T
    ): State<T> {
        val scope = rememberCoroutineScope()
        val resourceEnvironment = rememberResourceEnvironment()
        return remember(resourceEnvironment) {
            val mutableState = mutableStateOf(getDefault())
            scope.launch(start = CoroutineStart.UNDISPATCHED) {
                mutableState.value = block()
            }
            mutableState
        }
    }

    /**
     * Builds a plain string representation of the text.
     * 
     * This suspending function resolves the text content from its source (raw, resource, etc.)
     * and returns it as a plain string. Any styling information will be lost.
     * 
     * @return A plain string representation of the text content.
     */
    public suspend fun buildString(): String {
        return when (val charSequence = build()) {
            is String -> charSequence
            is AnnotatedString -> charSequence.toString() // We drop any style here
            else -> ""
        }
    }

    /**
     * Builds a plain string representation of the text in a composable context.
     * 
     * This composable function resolves the text content from its source and returns
     * it as a plain string. The result is remembered based on resource environment changes.
     * 
     * Note: This function launches a coroutine to load the resources asynchronously.
     * 
     * @return A plain string representation of the text content.
     */
    @Composable
    public override fun buildStringComposable(): String {
        val string by rememberResourceState({ "" }) {
            buildString()
        }
        return string
    }

    /**
     * Builds an annotated string representation of the text.
     * 
     * This suspending function resolves the text content from its source (raw, resource, etc.)
     * and returns it as an [AnnotatedString] that preserves styling information.
     * 
     * @return An [AnnotatedString] representation of the text content with styling.
     */
    public suspend fun buildAnnotatedString(): AnnotatedString {
        return when (val charSequence = build()) {
            is String -> AnnotatedString(charSequence)
            is AnnotatedString -> charSequence
            else -> AnnotatedString("")
        }
    }

    /**
     * Builds an annotated string representation of the text in a composable context.
     * 
     * This composable function resolves the text content from its source and returns
     * it as an [AnnotatedString] with styling. The result is remembered based on
     * resource environment changes.
     * 
     * Note: This function launches a coroutine to load the resources asynchronously.
     * 
     * @return An [AnnotatedString] representation of the text content with styling.
     */
    @Composable
    public override fun buildAnnotatedStringComposable(): AnnotatedString {
        val annotatedString by rememberResourceState({ AnnotatedString("") }) {
            buildAnnotatedString()
        }
        return annotatedString
    }

    protected suspend fun resolveArg(arg: Any): Any = when (arg) {
        is UIText -> arg.build()
        else -> arg
    }

    protected fun hasAnnotations(
        args: List<Pair<Any, List<UITextAnnotation>>>,
        resolvedArgs: List<Pair<Any, List<UITextAnnotation>>>,
        baseAnnotations: List<UITextAnnotation>
    ): Boolean {
        if (baseAnnotations.isNotEmpty()) return true
        if (args.any { it.second.isNotEmpty() }) return true
        return resolvedArgs.any { it.first is AnnotatedString }
    }

    internal class Raw(private val text: CharSequence) : UIText() {
        override suspend fun build(): CharSequence {
            return text
        }
    }

    internal class Res(
        private val stringResource: StringResource,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            if (args.isEmpty() && baseAnnotations.isEmpty()) {
                return getString(stringResource)
            }
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = hasAnnotations(
                args = args,
                resolvedArgs = resolvedArgs,
                baseAnnotations = baseAnnotations
            )
            return if (annotated) {
                UITextUtil.buildAnnotatedStringWithComposeMultiplatformStringResourceRules(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        getString(stringResource)
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                val argValues = Array(resolvedArgs.size) { i -> 
                    resolvedArgs[i].first 
                }
                getString(stringResource, *argValues)
            } else {
                getString(stringResource)
            }
        }
    }

    internal class PluralRes(
        private val pluralStringResource: PluralStringResource,
        private val quantity: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            if (args.isEmpty() && baseAnnotations.isEmpty()) {
                return getPluralString(pluralStringResource, quantity)
            }
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = hasAnnotations(
                args = args,
                resolvedArgs = resolvedArgs,
                baseAnnotations = baseAnnotations
            )
            return if (annotated) {
                UITextUtil.buildAnnotatedStringWithComposeMultiplatformStringResourceRules(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        getPluralString(pluralStringResource, quantity)
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                val argValues = Array(resolvedArgs.size) { i -> 
                    resolvedArgs[i].first 
                }
                getPluralString(pluralStringResource, quantity, *argValues)
            } else {
                getPluralString(pluralStringResource, quantity)
            }
        }
    }

    internal class Compound(
        private val components: List<UIText>
    ) : UIText() {
        override suspend fun build(): CharSequence {
            if (components.isEmpty()) {
                return ""
            }
            if (components.size == 1) {
                return components[0].build()
            }
            val resolvedComponents = components.map { it.build() }
            return UITextUtil.concat(resolvedComponents)
        }
    }
}