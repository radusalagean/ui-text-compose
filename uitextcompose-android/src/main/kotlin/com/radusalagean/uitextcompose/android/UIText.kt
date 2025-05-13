package com.radusalagean.uitextcompose.android

import android.content.Context
import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import com.radusalagean.uitextcompose.core.UITextAnnotation
import com.radusalagean.uitextcompose.core.UITextBase
import com.radusalagean.uitextcompose.core.UITextUtil

public sealed class UIText : UITextBase {

    protected abstract fun build(context: Context): CharSequence

    public fun buildString(context: Context): String {
        return when (val charSequence = build(context)) {
            is String -> charSequence
            is AnnotatedString -> charSequence.toString() // We drop any style here
            else -> ""
        }
    }

    @Composable
    override fun buildStringComposable(): String {
        return buildString(LocalContext.current)
    }

    public fun buildAnnotatedString(context: Context): AnnotatedString {
        return when (val charSequence = build(context)) {
            is String -> AnnotatedString(charSequence)
            is AnnotatedString -> charSequence
            else -> AnnotatedString("")
        }
    }

    @Composable
    override fun buildAnnotatedStringComposable(): AnnotatedString {
        return buildAnnotatedString(LocalContext.current)
    }

    protected fun resolveArg(context: Context, arg: Any): Any = when (arg) {
        is UIText -> arg.build(context)
        else -> arg
    }

    protected fun Resources.getStringWithPlaceholders(
        @StringRes resId: Int,
        placeholdersCount: Int
    ): String = getString(
        resId,
        *UITextUtil.generatePlaceholderArgs(placeholdersCount)
    )

    protected fun Resources.getQuantityStringWithPlaceholders(
        @PluralsRes resId: Int,
        quantity: Int,
        placeholdersCount: Int
    ): String = getQuantityString(
        resId,
        quantity,
        *UITextUtil.generatePlaceholderArgs(placeholdersCount)
    )

    public class Raw(private val text: CharSequence) : UIText() {
        override fun build(context: Context): CharSequence {
            return text
        }
    }

    public class Res(
        @StringRes private val resId: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override fun build(context: Context): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(context, it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                UITextUtil.buildAnnotatedString(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        context.resources.getStringWithPlaceholders(
                            resId = resId,
                            placeholdersCount = args.size
                        )
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                context.getString(resId, *resolvedArgs.map { it.first }.toTypedArray())
            } else {
                context.getString(resId)
            }
        }
    }

    public class PluralRes(
        @PluralsRes private val resId: Int,
        private val quantity: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override fun build(context: Context): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(context, it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                UITextUtil.buildAnnotatedString(
                    resolvedArgs = resolvedArgs,
                    baseAnnotations = baseAnnotations,
                    baseStringProvider = {
                        context.resources.getQuantityStringWithPlaceholders(
                            resId = resId,
                            quantity = quantity,
                            placeholdersCount = args.size
                        )
                    }
                )
            } else if (resolvedArgs.isNotEmpty()) {
                context.resources.getQuantityString(resId, quantity,
                    *resolvedArgs.map { it.first }.toTypedArray())
            } else {
                context.resources.getQuantityString(resId, quantity)
            }
        }
    }

    public class Compound(
        private val components: List<UIText>
    ) : UIText() {
        override fun build(context: Context): CharSequence {
            val resolvedComponents = components.map { it.build(context) }
            return UITextUtil.concat(resolvedComponents)
        }
    }
} 