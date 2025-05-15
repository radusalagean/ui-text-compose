package com.radusalagean.uitextcompose.multiplatform

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.withLink
import com.radusalagean.uitextcompose_multiplatform.generated.resources.Res
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res_with_arg
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_plural
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res_with_multiple_args
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_plural_with_multiple_args
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res_with_mixed_placeholders_1
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res_with_mixed_placeholders_2
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_res_with_mixed_placeholders_and_some_escaped
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_plural_with_mixed_placeholders_1
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_plural_with_mixed_placeholders_2
import com.radusalagean.uitextcompose_multiplatform.generated.resources.test_plural_with_mixed_placeholders_and_some_escaped
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class UITextTest {

    @get:Rule
    val rule = createComposeRule()
    
    @Test
    fun raw() = runTest {
        // Given
        val uiText = UIText { raw("This is a raw string") }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("This is a raw string", result)
    }

    @Test
    fun res() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res)
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("Instrumented test string", result)
    }
    
    @Test
    fun res_with_arg() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res_with_arg) {
                arg("instrumented")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("Hello from instrumented test!", result)
    }
    
    @Test
    fun res_with_UIText_raw_arg() = runTest {
        // Given
        val nestedText = UIText { raw("nested") }
        val uiText = UIText {
            res(Res.string.test_res_with_arg) {
                arg(nestedText)
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("Hello from nested test!", result)
    }

    @Test
    fun res_with_UIText_res_arg() = runTest {
        // Given
        val nestedText = UIText { res(Res.string.test_res) }
        val uiText = UIText {
            res(Res.string.test_res_with_arg) {
                arg(nestedText)
            }
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("Hello from Instrumented test string test!", result)
    }

    @Test
    fun res_with_UIText_plural_res_arg() = runTest {
        // Given
        val nestedText = UIText { pluralRes(Res.plurals.test_plural, 8) }
        val uiText = UIText {
            res(Res.string.test_res_with_arg) {
                arg(nestedText)
            }
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("Hello from 8 test items test!", result)
    }
    
    @Test
    fun res_with_multiple_types_of_args() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res_with_multiple_args) {
                arg("User")
                arg("encountered")
                arg(
                    UIText { res(Res.string.test_res) }
                )
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("User has encountered Instrumented test string", result)
    }
    
    @Test
    fun plural_res_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 1)
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1 test item", result)
    }
    
    @Test
    fun plural_res_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 5)
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("5 test items", result)
    }
    
    @Test
    fun plural_res_with_string_arg_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 1) {
                arg("custom")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("custom test item", result)
    }
    
    @Test
    fun plural_res_with_string_arg_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 5) {
                arg("custom")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("custom test items", result)
    }
    
    @Test
    fun plural_res_with_UIText_arg_single() = runTest {
        // Given
        val nestedText = UIText { raw("dynamic") }
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 1) {
                arg(nestedText)
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("dynamic test item", result)
    }
    
    @Test
    fun plural_res_with_UIText_arg_multiple() = runTest {
        // Given
        val nestedText = UIText { raw("dynamic") }
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 5) {
                arg(nestedText)
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("dynamic test items", result)
    }
    
    @Test
    fun plural_res_with_annotated_arg_single() = runTest {
        // Given
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 1) {
                arg("bold") {
                    +boldStyle
                }
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(boldStyle) {
                append("bold")
            }
            append(" test item")
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun plural_res_with_annotated_arg_multiple() = runTest {
        // Given
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 5) {
                arg("bold") {
                    +boldStyle
                }
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(boldStyle) {
                append("bold")
            }
            append(" test items")
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun plural_res_with_base_annotation_single() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural, 1) {
                +redStyle
                arg("special")
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(redStyle) {
                append("special test item")
            }
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun plural_res_with_multiple_args_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_multiple_args, 1) {
                arg("User")
                arg("rare")
                arg("special")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("User found rare special item", result)
    }
    
    @Test
    fun plural_res_with_multiple_args_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_multiple_args, 3) {
                arg("User")
                arg("rare")
                arg("special")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("User found rare special items", result)
    }
    
    @Test
    fun plural_res_with_complex_styling_and_args() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val italicStyle = SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_multiple_args, 5) {
                +redStyle
                arg("Explorer") {
                    +boldStyle
                }
                arg(UIText { raw("legendary") }) {
                    +italicStyle
                }
                arg("treasure")
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(redStyle) {
                withStyle(boldStyle) {
                    append("Explorer")
                }
                append(" found ")
                withStyle(italicStyle) {
                    append("legendary")
                }
                append(" treasure items")
            }
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun compound_text() = runTest {
        // Given
        val uiText = UIText {
            raw("First part. ")
            raw("Second part.")
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("First part. Second part.", result)
    }
    
    @Test
    fun compound_with_resources() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res)
            raw(". ")
            pluralRes(Res.plurals.test_plural, 3)
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("Instrumented test string. 3 test items", result)
    }
    
    @Test
    fun annotations_span_style() = runTest {
        // Given
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val uiText = UIText {
            res(Res.string.test_res) {
                +boldStyle
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(boldStyle) {
                append("Instrumented test string")
            }
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun annotations_paragraph_style() = runTest {
        // Given
        val centerAlignStyle = ParagraphStyle(textAlign = TextAlign.Center)
        val uiText = UIText {
            res(Res.string.test_res) {
                +centerAlignStyle
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(centerAlignStyle) {
                append("Instrumented test string")
            }
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun annotations_link() = runTest {
        // Given
        val linkAnnotation = LinkAnnotation.Url(url = "https://example.com")
        val uiText = UIText {
            res(Res.string.test_res) {
                +linkAnnotation
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withLink(linkAnnotation) {
                append("Instrumented test string")
            }
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun annotations_on_args() = runTest {
        // Given
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val uiText = UIText {
            res(Res.string.test_res_with_arg) {
                arg("highlighted") {
                    +boldStyle
                }
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            append("Hello from ")
            withStyle(boldStyle) {
                append("highlighted")
            }
            append(" test!")
        }
        assertEquals(expected, result)
    }
    
    @Test
    fun empty_UIText() = runTest {
        // Given
        val uiText = UIText { }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("", result)
    }
    
    @Test
    fun build_string_composable() {
        rule.setContent {
            // Given
            val uiText = UIText { 
                res(Res.string.test_res)
            }
            
            // When
            val result = uiText.buildStringComposable()
            
            // Then
            assertEquals("Instrumented test string", result)
        }
    }
    
    @Test
    fun build_annotated_string_composable() {
        rule.setContent {
            // Given
            val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
            val uiText = UIText {
                res(Res.string.test_res) {
                    +boldStyle
                }
            }
            
            // When
            val result = uiText.buildAnnotatedStringComposable()
            
            // Then
            val expected = buildAnnotatedString {
                withStyle(boldStyle) {
                    append("Instrumented test string")
                }
            }
            assertEquals(expected, result)
        }
    }
    
    @Test
    fun mixing_annotations_and_args() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
        val lightStyle = SpanStyle(fontWeight = FontWeight.Light)
        val uiText = UIText {
            res(Res.string.test_res_with_multiple_args) {
                +redStyle
                arg("User") {
                    +boldStyle
                }
                arg(5.toString())
                arg(UIText { 
                    raw("test") 
                    raw(" items")
                }) {
                    +lightStyle
                }
            }
        }
        
        // When
        val result = uiText.buildAnnotatedString()
        
        // Then
        val expected = buildAnnotatedString {
            withStyle(redStyle) {
                withStyle(boldStyle) {
                    append("User")
                }
                append(" has ")
                append("5")
                append(" ")
                withStyle(lightStyle) {
                    append("test items")
                }
            }
        }
        assertEquals(expected, result)
    }

    @Test
    fun res_with_mixed_placeholders_1() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_1) {
                arg("a")
                arg("b")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: %s 2: a 3: %s", result)
    }

    @Test
    fun res_with_mixed_placeholders_1_annotated() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_1) {
                arg("a") {
                    +redStyle
                }
                arg("b")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %s 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: %s")
        }
        assertEquals(expected, result)
    }

    @Test
    fun res_with_mixed_placeholders_2() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_2) {
                arg("a")
                arg("b")
                arg("c")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: b 2: a 3: a 4: %s 5: c 6: %s", result)
    }

    @Test
    fun res_with_mixed_placeholders_2_annotated() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_2) {
                arg("a")
                arg("b")
                arg("c") {
                    +redStyle
                }
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: b 2: a 3: a 4: %s 5: ")
            withStyle(redStyle) {
                append("c")
            }
            append(" 6: %s")
        }
        assertEquals(expected, result)
    }

    @Test
    fun res_with_mixed_placeholders_and_some_escaped() = runTest {
        // Given
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_and_some_escaped) {
                arg("a")
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("1: %b 2: a 3: a 4: %%s 5: c 6: %s", result)
    }

    @Test
    fun res_with_mixed_placeholders_and_some_escaped_annotated() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            res(Res.string.test_res_with_mixed_placeholders_and_some_escaped) {
                arg("a") {
                    +redStyle
                }
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %b 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 4: %%s 5: c 6: %s")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_1_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_1, 1) {
                arg("a")
                arg("b")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: %s 2: a 3: %s item", result)
    }

    @Test
    fun plural_with_mixed_placeholders_1_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_1, 5) {
                arg("a")
                arg("b")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: %s 2: a 3: %s items", result)
    }

    @Test
    fun plural_with_mixed_placeholders_1_annotated_single() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_1, 1) {
                arg("a") {
                    +redStyle
                }
                arg("b")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %s 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: %s item")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_1_annotated_multiple() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_1, 5) {
                arg("a") {
                    +redStyle
                }
                arg("b")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %s 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: %s items")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_2_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_2, 1) {
                arg("a")
                arg("b")
                arg("c")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: b 2: a 3: a 4: %s 5: c 6: %s item", result)
    }

    @Test
    fun plural_with_mixed_placeholders_2_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_2, 5) {
                arg("a")
                arg("b")
                arg("c")
            }
        }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("1: b 2: a 3: a 4: %s 5: c 6: %s items", result)
    }

    @Test
    fun plural_with_mixed_placeholders_2_annotated_single() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_2, 1) {
                arg("a")
                arg("b")
                arg("c") {
                    +redStyle
                }
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: b 2: a 3: a 4: %s 5: ")
            withStyle(redStyle) {
                append("c")
            }
            append(" 6: %s item")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_2_annotated_multiple() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_2, 5) {
                arg("a")
                arg("b")
                arg("c") {
                    +redStyle
                }
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: b 2: a 3: a 4: %s 5: ")
            withStyle(redStyle) {
                append("c")
            }
            append(" 6: %s items")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_and_some_escaped_single() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_and_some_escaped, 1) {
                arg("a")
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("1: %b 2: a 3: a 4: %%s 5: c 6: %s item", result)
    }

    @Test
    fun plural_with_mixed_placeholders_and_some_escaped_multiple() = runTest {
        // Given
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_and_some_escaped, 5) {
                arg("a")
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildString()

        // Then
        assertEquals("1: %b 2: a 3: a 4: %%s 5: c 6: %s items", result)
    }

    @Test
    fun plural_with_mixed_placeholders_and_some_escaped_annotated_single() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_and_some_escaped, 1) {
                arg("a") {
                    +redStyle
                }
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %b 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 4: %%s 5: c 6: %s item")
        }
        assertEquals(expected, result)
    }

    @Test
    fun plural_with_mixed_placeholders_and_some_escaped_annotated_multiple() = runTest {
        // Given
        val redStyle = SpanStyle(color = Color.Red)
        val uiText = UIText {
            pluralRes(Res.plurals.test_plural_with_mixed_placeholders_and_some_escaped, 5) {
                arg("a") {
                    +redStyle
                }
                arg("b")
                arg("c")
            }
        }

        // When
        val result = uiText.buildAnnotatedString()

        // Then
        val expected = buildAnnotatedString {
            append("1: %b 2: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 3: ")
            withStyle(redStyle) {
                append("a")
            }
            append(" 4: %%s 5: c 6: %s items")
        }
        assertEquals(expected, result)
    }
}