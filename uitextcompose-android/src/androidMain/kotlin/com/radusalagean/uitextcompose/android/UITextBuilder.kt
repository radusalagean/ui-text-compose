package com.radusalagean.uitextcompose.android

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.radusalagean.uitextcompose.core.InternalApi
import com.radusalagean.uitextcompose.core.ResBuilder
import com.radusalagean.uitextcompose.core.UITextBuilderBase
import com.radusalagean.uitextcompose.core.UITextDslMarker

public fun UIText(block: UITextBuilder.() -> Unit): UIText = UITextBuilder().apply(block).build()

@UITextDslMarker
@OptIn(InternalApi::class)
public class UITextBuilder : UITextBuilderBase<UIText> {
    private val components = mutableListOf<UIText>()

    override fun raw(text: CharSequence) {
        components += UIText.Raw(text)
    }

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

    override fun build(): UIText = when (components.size) {
        0 -> UIText.Raw("")
        1 -> components[0]
        else -> UIText.Compound(components)
    }
}
