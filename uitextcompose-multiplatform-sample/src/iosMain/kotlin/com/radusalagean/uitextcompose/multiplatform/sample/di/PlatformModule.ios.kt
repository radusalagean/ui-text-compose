package com.radusalagean.uitextcompose.multiplatform.sample.di

import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManager
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManagerIOS
import org.koin.dsl.module

actual val platformModule = module {
    single<LanguageManager> { LanguageManagerIOS() }
}