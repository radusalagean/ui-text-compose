package com.radusalagean.uitextcompose.kmp.sample.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.radusalagean.uitextcompose.kmp.UIText

@Composable
fun SectionHeader(
    text: UIText,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.buildStringComposable(),
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge
    )
}