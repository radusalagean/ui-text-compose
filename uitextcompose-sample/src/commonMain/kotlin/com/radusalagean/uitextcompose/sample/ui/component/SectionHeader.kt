package com.radusalagean.uitextcompose.sample.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.radusalagean.uitextcompose.UIText

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