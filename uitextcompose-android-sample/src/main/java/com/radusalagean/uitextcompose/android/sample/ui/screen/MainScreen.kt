package com.radusalagean.uitextcompose.android.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.radusalagean.uitextcompose.android.sample.R
import com.radusalagean.uitextcompose.android.sample.ui.component.ExampleEntry
import com.radusalagean.uitextcompose.android.sample.ui.component.Section

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    MaterialTheme {
        Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
            Column(Modifier.fillMaxSize().padding(innerPadding)) {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.app_name))
                    }
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 42.dp)
                ) {
                    Section(title = viewModel.languageSectionTitle) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            viewModel.languageOptions.forEachIndexed { index, entry ->
                                InputChip(
                                    selected = index == viewModel.selectedLanguageIndex,
                                    onClick = { viewModel.onLanguageSelected(entry.languageCode) },
                                    label = {
                                        Text(entry.uiText.buildAnnotatedStringComposable())
                                    }
                                )
                            }
                        }
                    }

                    Section(title = viewModel.examplesSectionTitle) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            viewModel.exampleEntries.forEach {
                                ExampleEntry(
                                    model = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}