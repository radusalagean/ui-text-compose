package com.radusalagean.uitextcompose.multiplatform

import com.radusalagean.uitextcompose.core.ResBuilder
import com.radusalagean.uitextcompose.core.UITextBuilderBase
import com.radusalagean.uitextcompose.core.UITextDslMarker
import org.jetbrains.compose.resources.PluralStringResource
import org.jetbrains.compose.resources.StringResource

public fun UIText(block: UITextBuilder.() -> Unit): UIText = UITextBuilder().apply(block).build()

@UITextDslMarker
public class UITextBuilder : UITextBuilderBase<UIText> {
    private val components = mutableListOf<UIText>()

    override fun raw(text: CharSequence) {
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

    override fun build(): UIText = when (components.size) {
        0 -> UIText.Raw("")
        1 -> components[0]
        else -> UIText.Compound(components)
    }
}
