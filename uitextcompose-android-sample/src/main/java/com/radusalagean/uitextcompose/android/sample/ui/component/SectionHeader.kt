package com.radusalagean.uitextcompose.android.sample.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.radusalagean.uitextcompose.android.UIText

@Composable
fun SectionHeader(
    text: UIText,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.buildAnnotatedStringComposable(),
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge
    )
}