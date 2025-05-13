package com.radusalagean.uitextcompose.multiplatform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.AnnotatedString
import com.radusalagean.uitextcompose.core.UITextAnnotation
import com.radusalagean.uitextcompose.core.UITextBase
import com.radusalagean.uitextcompose.core.UITextUtil
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getPluralString
import org.jetbrains.compose.resources.getString

public sealed class UIText : UITextBase {

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

    public suspend fun buildString(): String {
        return when (val charSequence = build()) {
            is String -> charSequence
            is AnnotatedString -> charSequence.toString() // We drop any style here
            else -> ""
        }
    }

    @Composable
    public override fun buildStringComposable(): String {
        val string by rememberResourceState({ "" }) {
            buildString()
        }
        return string
    }

    public suspend fun buildAnnotatedString(): AnnotatedString {
        return when (val charSequence = build()) {
            is String -> AnnotatedString(charSequence)
            is AnnotatedString -> charSequence
            else -> AnnotatedString("")
        }
    }

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

    protected suspend fun getStringWithPlaceholders(
        stringResource: StringResource,
        placeholdersCount: Int
    ): String = getString(
        stringResource,
        *UITextUtil.generatePlaceholderArgs(placeholdersCount)
    )

    protected suspend fun getQuantityStringWithPlaceholders(
        pluralStringResource: PluralStringResource,
        quantity: Int,
        placeholdersCount: Int
    ): String = getPluralString(
        pluralStringResource,
        quantity,
        *UITextUtil.generatePlaceholderArgs(placeholdersCount)
    )

    public class Raw(private val text: CharSequence) : UIText() {
        override suspend fun build(): CharSequence {
            return text
        }
    }

    public class Res(
        private val stringResource: StringResource,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                UITextUtil.buildAnnotatedStringSuspend(
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

    public class PluralRes(
        private val pluralStringResource: PluralStringResource,
        private val quantity: Int,
        private val args: List<Pair<Any, List<UITextAnnotation>>>,
        private val baseAnnotations: List<UITextAnnotation>
    ) : UIText() {

        override suspend fun build(): CharSequence {
            val resolvedArgs = args.map {
                resolveArg(it.first) to it.second
            }
            val annotated = baseAnnotations.isNotEmpty() ||
                    args.any { it.second.isNotEmpty() } ||
                    resolvedArgs.any { it.first is AnnotatedString }

            return if (annotated) {
                UITextUtil.buildAnnotatedStringSuspend(
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

    public class Compound(
        private val components: List<UIText>
    ) : UIText() {
        override suspend fun build(): CharSequence {
            val resolvedComponents = components.map { it.build() }
            return UITextUtil.concat(resolvedComponents)
        }
    }
}