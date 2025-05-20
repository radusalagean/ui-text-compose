package com.radusalagean.uitextcompose.android

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.radusalagean.uitextcompose.core.InternalApi
import com.radusalagean.uitextcompose.core.UITextAnnotation
import com.radusalagean.uitextcompose.core.UITextBase
import com.radusalagean.uitextcompose.core.UITextUtil

/**
 * Implementation of [UITextBase] for handling text in Android applications
 *  using Android String Resources.
 * 
 * This class provides a way to work with text from various sources such as:
 * - Raw text strings
 * - Android string resources
 * - Android plural resources
 * 
 * It handles string formatting with placeholders and supports styling through span styles,
 * paragraph styles, and link annotations.
 * 
 * Example usage:
 * ```
 * // Create a UIText instance using the DSL
 * val text = UIText {
 *     res(R.string.greeting) {
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

    protected abstract fun build(context: Context): CharSequence

    @Composable
    private fun <T> rememberResourceState(
        block: () -> T
    ): T {
        val configuration = LocalConfiguration.current
        return remember(configuration) {
            block()
        }
    }

    /**
     * Builds a plain string representation of the text using the provided context.
     * 
     * This method resolves the text content from its source (raw, resource, etc.) and
     * returns it as a plain string. Any styling information will be lost.
     * 
     * @param context The Android context to use for resolving resources.
     * @return A plain string representation of the text content.
     */
    public fun buildString(context: Context): String {
        return when (val charSequence = build(context)) {
            is String -> charSequence
            is AnnotatedString -> charSequence.toString() // We drop any style here
            else -> ""
        }
    }

    /**
     * Builds a plain string representation of the text in a composable context.
     * 
     * This composable function retrieves the current Android context and configuration,
     * then resolves the text content from its source and returns it as a plain string.
     * The result is remembered based on configuration changes.
     * 
     * @return A plain string representation of the text content.
     */
    @Composable
    public override fun buildStringComposable(): String {
        val context = LocalContext.current
        return rememberResourceState {
            buildString(context)
        }
    }

    /**
     * Builds an annotated string representation of the text using the provided context.
     * 
     * This method resolves the text content from its source (raw, resource, etc.) and
     * returns it as an [AnnotatedString] that preserves styling information.
     * 
     * @param context The Android context to use for resolving resources.
     * @return An [AnnotatedString] representation of the text content with styling.
     */
    public fun buildAnnotatedString(context: Context): AnnotatedString {
        return when (val charSequence = build(context)) {
            is String -> AnnotatedString(charSequence)
            is AnnotatedString -> charSequence
            else -> AnnotatedString("")
        }
    }

    /**
     * Builds an annotated string representation of the text in a composable context.
     * 
     * This composable function retrieves the current Android context and configuration,
     * then resolves the text content from its source and returns it as an [AnnotatedString] with styling.
     * The result is remembered based on configuration changes.
     * 
     * @return An [AnnotatedString] representation of the text content with styling.
     */
    @Composable
    public override fun buildAnnotatedStringComposable(): AnnotatedString {
        val context = LocalContext.current
        return rememberResourceState {
            buildAnnotatedString(context)
        }
    }

    protected fun resolveArg(context: Context, arg: Any): Any = when (arg) {
        is UIText -> arg.build(context)
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
        override fun build(context: Context): CharSequence {
            return text
        }
    }

    internal class Res(
        @StringRes private val resId: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override fun build(context: Context): CharSequence {
            if (args.isEmpty() && baseAnnotations.isEmpty()) {
                return context.getString(resId)
            }
            val resolvedArgs = args.map {
                resolveArg(context, it.first) to it.second
            }
            val annotated = hasAnnotations(
                args = args,
                resolvedArgs = resolvedArgs,
                baseAnnotations = baseAnnotations
            )
            return if (annotated) {
                UITextUtil.buildAnnotatedStringWithAndroidStringResourceRules(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        context.getString(resId)
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                val argValues = Array(resolvedArgs.size) { i -> 
                    resolvedArgs[i].first 
                }
                context.getString(resId, *argValues)
            } else {
                context.getString(resId)
            }
        }
    }

    internal class PluralRes(
        @PluralsRes private val resId: Int,
        private val quantity: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override fun build(context: Context): CharSequence {
            if (args.isEmpty() && baseAnnotations.isEmpty()) {
                return context.resources.getQuantityString(resId, quantity)
            }
            val resolvedArgs = args.map {
                resolveArg(context, it.first) to it.second
            }
            val annotated = hasAnnotations(
                args = args,
                resolvedArgs = resolvedArgs,
                baseAnnotations = baseAnnotations
            )
            return if (annotated) {
                UITextUtil.buildAnnotatedStringWithAndroidStringResourceRules(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        context.resources.getQuantityString(resId, quantity)
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                val argValues = Array(resolvedArgs.size) { i -> 
                    resolvedArgs[i].first 
                }
                context.resources.getQuantityString(resId, quantity, *argValues)
            } else {
                context.resources.getQuantityString(resId, quantity)
            }
        }
    }

    internal class Compound(
        private val components: List<UIText>
    ) : UIText() {
        override fun build(context: Context): CharSequence {
            if (components.isEmpty()) {
                return ""
            }
            if (components.size == 1) {
                return components[0].build(context)
            }
            val resolvedComponents = components.map { it.build(context) }
            return UITextUtil.concat(resolvedComponents)
        }
    }
}