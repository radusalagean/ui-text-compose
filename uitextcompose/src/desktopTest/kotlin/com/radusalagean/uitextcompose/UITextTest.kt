package com.radusalagean.uitextcompose

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
import com.radusalagean.uitextcompose.generated.resources.Res
import com.radusalagean.uitextcompose.generated.resources.test_res
import com.radusalagean.uitextcompose.generated.resources.test_res_with_arg
import com.radusalagean.uitextcompose.generated.resources.test_plural
import com.radusalagean.uitextcompose.generated.resources.test_res_with_multiple_args
import com.radusalagean.uitextcompose.generated.resources.test_plural_with_multiple_args
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
    fun `res with arg`() = runTest {
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
    fun `res with UIText raw arg`() = runTest {
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
    fun `res with UIText res arg`() = runTest {
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
    fun `res with UIText plural res arg`() = runTest {
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
    fun `res with multiple types of args`() = runTest {
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
    fun `plural res single`() = runTest {
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
    fun `plural res multiple`() = runTest {
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
    fun `plural res with string arg single`() = runTest {
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
    fun `plural res with string arg multiple`() = runTest {
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
    fun `plural res with UIText arg single`() = runTest {
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
    fun `plural res with UIText arg multiple`() = runTest {
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
    fun `plural res with annotated arg single`() = runTest {
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
    fun `plural res with annotated arg multiple`() = runTest {
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
    fun `plural res with base annotation single`() = runTest {
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
    fun `plural res with multiple args single`() = runTest {
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
    fun `plural res with multiple args multiple`() = runTest {
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
    fun `plural res with complex styling and args`() = runTest {
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
    fun `compound text`() = runTest {
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
    fun `compound with resources`() = runTest {
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
    fun `annotations span style`() = runTest {
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
    fun `annotations paragraph style`() = runTest {
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
    fun `annotations link`() = runTest {
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
    fun `annotations on args`() = runTest {
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
    fun `empty UIText`() = runTest {
        // Given
        val uiText = UIText { }
        
        // When
        val result = uiText.buildString()
        
        // Then
        assertEquals("", result)
    }
    
    @Test
    fun `build string composable`() {
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
    fun `build annotated string composable`() {
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
    fun `mixing annotations and args`() = runTest {
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
}