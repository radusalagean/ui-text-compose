package com.radusalagean.uitextcompose.android

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.radusalagean.uitextcompose.core.InternalApi
import com.radusalagean.uitextcompose.core.ResBuilder
import com.radusalagean.uitextcompose.core.UITextBuilderBase
import com.radusalagean.uitextcompose.core.UITextDslMarker

/**
 * Creates a new [UIText] instance using a DSL builder.
 * 
 * This function provides a concise way to create text from various sources including
 * raw text and Android string resources. It supports styling and formatting through
 * a type-safe DSL.
 * 
 * Example:
 * ```
 * // Create from raw text
 * val text1 = UIText {
 *     raw("Hello, World!")
 * }
 * 
 * // Create from string resource
 * val text2 = UIText {
 *     res(R.string.greeting) {
 *         arg("User")
 *     }
 * }
 * 
 * // Create from plural resource
 * val text3 = UIText {
 *     pluralRes(R.plurals.items_count, 5)
 * }
 * 
 * // Combine multiple sources
 * val text4 = UIText {
 *     raw("Hello, ")
 *     res(R.string.user_name)
 *     raw("!")
 * }
 * ```
 * 
 * @param block The builder block for configuring the text.
 * @return A new [UIText] instance.
 */
public fun UIText(block: UITextBuilder.() -> Unit): UIText = UITextBuilder().apply(block).build()

/**
 * Builder for creating [UIText] instances specifically for Android.
 * 
 * This builder allows creating text from multiple sources including raw text and
 * Android string resources. It supports styling and formatting through annotations.
 * 
 * The builder collects text components and combines them into a single [UIText] instance.
 * 
 * @see UIText
 * @see raw
 * @see res
 * @see pluralRes
 */
@UITextDslMarker
@OptIn(InternalApi::class)
public class UITextBuilder : UITextBuilderBase<UIText> {
    private val components = mutableListOf<UIText>()

    /**
     * Adds raw text content to the builder.
     * 
     * Use this method to add plain text that doesn't come from a resource.
     * 
     * Example:
     * ```
     * UIText {
     *     raw("Hello, World!")
     * }
     * ```
     * 
     * @param text The text content to add.
     */
    override fun raw(text: CharSequence) {
        components += UIText.Raw(text)
    }

    /**
     * Adds text from an Android string resource.
     * 
     * This method resolves text from an Android string resource ID and allows adding
     * arguments for placeholders in the resource string.
     * 
     * Example:
     * ```
     * UIText {
     *     res(R.string.greeting) {
     *         arg("User") {
     *             +SpanStyle(color = Color.Blue)
     *         }
     *     }
     * }
     * ```
     * 
     * @param resId The Android string resource ID to use.
     * @param resBuilder Optional builder for configuring arguments and styling.
     */
    public fun res(
        @StringRes resId: Int,
        resBuilder: ResBuilder.() -> Unit = { }
    ) {
        val config = ResBuilder().apply(resBuilder).buildResConfig()
        components += UIText.Res(
            resId = resId,
            args = config.args,
            baseAnnotations = config.annotations
        )
    }

    /**
     * Adds text from an Android plural resource.
     * 
     * This method resolves text from an Android plural resource ID based on the given quantity
     * and allows adding arguments for placeholders in the resource string.
     * 
     * By default, the quantity is automatically added as the first argument.
     * 
     * Example:
     * ```
     * UIText {
     *     pluralRes(R.plurals.items_count, 5) {
     *         // Additional arguments if needed
     *         arg("category") {
     *             +SpanStyle(fontWeight = FontWeight.Bold)
     *         }
     *     }
     * }
     * ```
     * 
     * @param resId The Android plural resource ID to use.
     * @param quantity The quantity value for selecting the appropriate plural form.
     * @param resBuilder Optional builder for configuring arguments and styling.
     */
    public fun pluralRes(
        @PluralsRes resId: Int,
        quantity: Int,
        resBuilder: ResBuilder.() -> Unit = {
            arg(quantity.toString())
        }
    ) {
        val config = ResBuilder().apply(resBuilder).buildResConfig()
        components += UIText.PluralRes(
            resId = resId,
            quantity = quantity,
            args = config.args,
            baseAnnotations = config.annotations
        )
    }

    /**
     * Builds and returns the final [UIText] instance.
     * 
     * This method creates an appropriate [UIText] instance based on the components
     * added to the builder:
     * - If no components were added, returns an empty text.
     * - If only one component was added, returns that component directly.
     * - If multiple components were added, returns a compound text that combines them.
     * 
     * @return The built [UIText] instance.
     */
    override fun build(): UIText = when (components.size) {
        0 -> UIText.Raw("")
        1 -> components[0]
        else -> UIText.Compound(components)
    }
}
