# UIText Compose

A Kotlin Multiplatform library for creating text blueprints in Compose applications, supporting both plain and styled text with String Resources integration.

## Overview

UIText Compose addresses a critical challenge in Compose UI applications: handling text resources efficiently while avoiding the [ViewModel antipattern](https://medium.com/androiddevelopers/locale-changes-and-the-androidviewmodel-antipattern-84eb677660d9) with locale changes.

The library provides a solution to a common problem: **ViewModels that directly expose localized strings become stale when the user changes their device language** because ViewModels survive configuration changes. This leads to partially localized apps showing obsolete text.

UIText Compose solves this by enabling:

1. **Proper separation of text definition from rendering** - Create text blueprints (containing resource IDs, not resolved strings) in your ViewModels, mappers, or data layers, then pass them to composables for locale-aware rendering.

2. **Automatic adaptation to configuration changes** - UIText instances automatically update when the locale or other configuration changes, ensuring your app is fully localized.

3. **Rich styling and formatting** - Add spans, paragraph styles, and link annotations to your text while maintaining the proper architecture.

4. **Composition of complex text patterns** - Combine text from multiple sources (raw text, string resources, plural resources) into a single text object.

This approach follows the best practice recommended by the Android team: exposing resource IDs from ViewModels rather than resolved strings, allowing the view layer to properly handle configuration changes.

## Getting Started

### For Android-only projects or Android modules in KMP projects

1. Add the dependency to your module's build.gradle.kts:
```kotlin
dependencies {
    implementation("com.radusalagean:uitextcompose-android:1.0.0")
}
```

2. Define the string resource:
```xml
<resources>
    <string name="greeting">Hi, %1$s!</string>
</resources>
```

3. Import the necessary classes:
```kotlin
import com.radusalagean.uitextcompose.android.UIText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color
```

4. Create a UIText instance and use it in your Composable:
```kotlin
class MyViewModel {
    val helloText = UIText {
        res(R.string.greeting) {
            arg("Radu")
        }
    }
}

@Composable
fun MyScreen(viewModel: MyViewModel) {
    Text(text = viewModel.helloText.buildStringComposable())
}
```

### For Compose Multiplatform projects with shared UI (KMP Projects)

#### Compatible platforms:
- Android
- iOS
- Desktop (JVM)
- Web (wasmJs)

#### Integration

1. Add the dependency to your module's build.gradle.kts:
```kotlin
commonMain.dependencies {
    implementation("com.radusalagean:uitextcompose-multiplatform:1.0.0")
}
```

2. Define the string resource:
```xml
<resources>
    <string name="greeting">Hi, %1$s!</string>
</resources>
```

3. Import the necessary classes:
```kotlin
import com.radusalagean.uitextcompose.multiplatform.UIText
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color
```

4. Create a UIText instance and use it in your Composable:
```kotlin
class MyViewModel {
    val helloText = UIText {
        res(Res.string.greeting) {
            arg("Radu")
        }
    }
}

@Composable
fun MyScreen(viewModel: MyViewModel) {
    Text(text = viewModel.helloText.buildStringComposable())
}
```

## Examples (replace `R.` with `Res.` for multiplatform usage)

### String resources used in the examples

```xml
<resources>
    <string name="greeting">Hi, %1$s!</string>
    <string name="shopping_cart_status">You have %1$s in your %2$s.</string>
    <string name="shopping_cart_status_insert_shopping_cart">shopping cart</string>
    <string name="proceed_to_checkout">Proceed to checkout</string>

    <string name="legal_footer_example">This is how you can create a %1$s and %2$s text with links.</string>
    <string name="legal_footer_example_insert_terms_of_service">Terms of Service</string>
    <string name="legal_footer_example_insert_privacy_policy">Privacy Policy</string>

    <plurals name="products">
        <item quantity="one">%1$s product</item>
        <item quantity="other">%1$s products</item>
    </plurals>
</resources>
```

### Raw text
Define:
```kotlin
val uiText = UIText {
    raw("Radu")
}
```
Use in composable:
```kotlin
Text(uiText.buildStringComposable())
```

### String resource
Define:
```kotlin
val uiText = UIText {
    res(R.string.greeting) {
        arg("Radu")
    }
}
```
Use in composable:
```kotlin
Text(uiText.buildStringComposable())
```

### Plural string resource
Define:
```kotlin
val uiText = UIText {
    pluralRes(R.plurals.products, 30)
}
```
Use in composable:
```kotlin
Text(uiText.buildStringComposable())
```

### String resource - annotated
Define:
```kotlin
val uiText = UIText {
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
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```

### Plural string resource - annotated
Define:
```kotlin
val uiText = UIText {
    pluralRes(R.plurals.products, 30) {
        arg(30.toString()) {
            +SpanStyle(color = CustomGreen)
        }
        +SpanStyle(fontWeight = FontWeight.Bold)
    }
}
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```

### Compound - example 1
Define:
```kotlin
val uiText = UIText {
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
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```


### Compound - example 2
Define:
```kotlin
val uiText = UIText {
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
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```


### Compound - example 3
Define:
```kotlin
val uiText = UIText {
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
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```

### Terms of Service & Privacy Policy
Define:
```kotlin
val uiText = UIText {
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
```
Use in composable:
```kotlin
Text(uiText.buildAnnotatedStringComposable())
```

## Compatibility of string resources

### Android Resources

UIText Compose for Android works with standard Android string resources as described in the [official documentation](https://developer.android.com/guide/topics/resources/string-resource).

It supports:
- String resource ids (`R.string.*`)
- Plural resource ids (`R.plurals.*`)

### Multiplatform Resources

For Kotlin Multiplatform projects, UIText Compose works with Compose Multiplatform's string resources as described in the [official documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources-usage.html#strings).

It supports:
- Type-safe string resources (`Res.string.*`)
- Type-safe plural resources (`Res.plurals.*`)


## Supported placeholders for string resources

- **uitextcompose-android**: `%s` (unnumbered) and `%1$s`, `%2$s`, ... (numbered) string placeholders
- **uitextcompose-multiplatform**: `%1$s`, `%2$s`, ... (numbered) string placeholders

## API Description

### Core Concepts

#### UITextBase

The base interface that defines methods for getting plain text or annotated text in your composables:

```kotlin
interface UITextBase {
    @Composable
    fun buildStringComposable(): String

    @Composable
    fun buildAnnotatedStringComposable(): AnnotatedString
}
```

#### UIText

The main class that implements `UITextBase`. There are two implementations:
- `com.radusalagean.uitextcompose.android.UIText` - For Android projects or Android modules in KMP projects
- `com.radusalagean.uitextcompose.multiplatform.UIText` - For Compose Multiplatform projects with shared UI


**Represents the blueprint, which is used by your composables to build the strings.**

Instances are created using the **DSL builder**:

```kotlin
val text = UIText {
    raw("Hello")
    // More builder methods here...
}
```

### Builder Methods

#### Raw Text
```kotlin
raw("Hello, World!")
```

#### String Resources (replace `R.` with `Res.` for multiplatform usage)
```kotlin
res(R.string.greeting) {
    // Optional arguments
    arg("User")
}
```

#### Plural Resources (replace `R.` with `Res.` for multiplatform usage)
```kotlin
pluralRes(R.plurals.items_count, 5)
```
...or, if you need more flexibility for your arguments
```kotlin
pluralRes(R.plurals.items_count, 5) {
    arg("5") {
        +SpanStyle(color = Color.Red)
    }
}
```

### Arguments

Types of args supported:
- `CharSequence` - for example: `String` or `AnnitatedString`
- other `UIText` instances

### Styling

You can apply styling to arguments and the base string resource:

```kotlin
res(R.string.greeting) {
    arg("Radu") {
        // Apply a span style to the argument
        +SpanStyle(
            color = Color.Blue,
            fontWeight = FontWeight.Bold
        )
    }
    // Apply a span style to the base string resource
    +SpanStyle(
        color = Color.Red
    )
}
```

⚠️ **Do not forget the `+` operator**

Types of styling supported:
- `SpanStyle` - For character-level styling (color, font weight, etc.)
- `ParagraphStyle` - For paragraph-level styling (alignment, indentation, etc.)
- `LinkAnnotation` - For adding clickable links


## Sample Apps
Sample apps are available in the `uitextcompose-android-sample` and `uitextcompose-multiplatform-sample` modules.

## Contributions
Found a bug or have a suggestion? Please open an [issue](https://github.com/radusalagean/ui-text-compose/issues).

## Support 🌟
If you use this library and enjoy it, please support it by **starring** it on GitHub. 🌟

## License
Apache License 2.0, see the [LICENSE](LICENSE) file for details.