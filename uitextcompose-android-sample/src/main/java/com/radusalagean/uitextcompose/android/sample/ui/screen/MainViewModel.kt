package com.radusalagean.uitextcompose.android.sample.ui.screen

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.radusalagean.uitextcompose.android.UIText
import com.radusalagean.uitextcompose.android.sample.R
import com.radusalagean.uitextcompose.android.sample.ui.component.ExampleEntryModel
import com.radusalagean.uitextcompose.android.sample.ui.component.LanguageOption
import com.radusalagean.uitextcompose.android.sample.ui.theme.CustomGreen

class MainViewModel : ViewModel() {

    // Section: Language
    val languageSectionTitle = UIText { res(R.string.section_title_language) }
    val languageOptions = listOf(
        LanguageOption(
            uiText = UIText { res(R.string.language_english) },
            languageCode = "en"
        ),
        LanguageOption(
            uiText = UIText { res(R.string.language_romanian) },
            languageCode = "ro"
        )
    )
    var selectedLanguageCode by mutableStateOf("")
    val selectedLanguageIndex: Int by derivedStateOf {
        languageOptions.indexOfFirst { it.languageCode == selectedLanguageCode }
    }

    fun syncSelectedLanguage() {
        val locales = AppCompatDelegate.getApplicationLocales()
        selectedLanguageCode = locales.get(0)?.language ?: "en"
    }

    fun onLanguageSelected(code: String) {
        val localesList = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(localesList)
    }

    // Section: Examples
    val examplesSectionTitle = UIText { res(R.string.section_title_examples) }
    val exampleEntries = listOf(
        ExampleEntryModel(
            label = "raw",
            value = UIText {
                raw("Radu")
            }
        ),
        ExampleEntryModel(
            label = "res",
            value = UIText {
                res(R.string.greeting) {
                    arg("Radu")
                }
            }
        ),
        ExampleEntryModel(
            label = "pluralRes",
            value = UIText {
                pluralRes(R.plurals.products, 30)
            }
        ),
        ExampleEntryModel(
            label = "res - annotated",
            value = UIText {
                res(R.string.shopping_cart_status) {
                    arg(
                        UIText {
                            pluralRes(R.plurals.products, 30)
                        }
                    )
                    arg(
                        UIText {
                            res(R.string.shopping_cart_status_insert_shopping_cart) {
                                +SpanStyle(color = Color.Red)
                            }
                        }
                    )
                }
            }
        ),
        ExampleEntryModel(
            label = "pluralRes - annotated",
            value = UIText {
                pluralRes(R.plurals.products, 30) {
                    arg(30.toString()) {
                        +SpanStyle(color = CustomGreen)
                    }
                    +SpanStyle(fontWeight = FontWeight.Bold)
                }
            }
        ),
        ExampleEntryModel(
            label = "compound - example 1",
            value = UIText {
                res(R.string.greeting) {
                    arg("Radu")
                }
                raw(" ")
                res(R.string.shopping_cart_status) {
                    arg(
                        UIText {
                            pluralRes(R.plurals.products, 30) {
                                arg(30.toString()) {
                                    +SpanStyle(color = CustomGreen)
                                }
                                +SpanStyle(fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    arg(
                        UIText {
                            res(R.string.shopping_cart_status_insert_shopping_cart) {
                                +SpanStyle(color = Color.Red)
                            }
                        }
                    )
                }
            }
        ),
        ExampleEntryModel(
            label = "compound - example 2",
            value = UIText {
                res(R.string.greeting) {
                    arg("Radu")
                }
                raw(" ")
                res(R.string.shopping_cart_status) {
                    arg(
                        UIText {
                            pluralRes(R.plurals.products, 30) {
                                arg(30.toString()) {
                                    +SpanStyle(color = CustomGreen)
                                }
                                +SpanStyle(fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    arg(
                        UIText {
                            res(R.string.shopping_cart_status_insert_shopping_cart)
                        }
                    ) {
                        +SpanStyle(color = Color.Red)
                    }
                }
                raw(" ")
                res(R.string.proceed_to_checkout) {
                    +LinkAnnotation.Url(
                        url = "https://example.com",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                }
            }
        ),
        ExampleEntryModel(
            label = "compound - example 3",
            value = UIText {
                res(R.string.greeting) {
                    arg("Radu")
                }
                res(R.string.shopping_cart_status) {
                    +ParagraphStyle()
                    arg(
                        UIText {
                            pluralRes(R.plurals.products, 30) {
                                +SpanStyle(fontWeight = FontWeight.Bold)
                                arg(30.toString()) {
                                    +SpanStyle(color = CustomGreen)
                                }
                            }
                        }
                    )
                    arg(
                        UIText {
                            res(R.string.shopping_cart_status_insert_shopping_cart)
                        }
                    ) {
                        +SpanStyle(color = Color.Red)
                    }
                }
                res(R.string.proceed_to_checkout) {
                    +LinkAnnotation.Url(
                        url = "https://example.com",
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = Color.Blue,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                    )
                }
            }
        ),
        ExampleEntryModel(
            label = "terms of service & privacy policy",
            value = UIText {
                val linkStyle = TextLinkStyles(
                    SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)
                )
                res(R.string.legal_footer_example) {
                    arg(
                        UIText {
                            res(R.string.legal_footer_example_insert_terms_of_service)
                        }
                    ) {
                        +LinkAnnotation.Url(
                            url = "https://radusalagean.com/example-terms-of-service/",
                            styles = linkStyle
                        )
                    }
                    arg(
                        UIText {
                            res(R.string.legal_footer_example_insert_privacy_policy)
                        }
                    ) {
                        +LinkAnnotation.Url(
                            url = "https://radusalagean.com/example-privacy-policy/",
                            styles = linkStyle
                        )
                    }
                }
            }
        )
    )
}