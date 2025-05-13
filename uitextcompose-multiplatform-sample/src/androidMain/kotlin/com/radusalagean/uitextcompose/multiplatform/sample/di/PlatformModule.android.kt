package com.radusalagean.uitextcompose.multiplatform.sample.di

import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManager
import com.radusalagean.uitextcompose.multiplatform.sample.util.LanguageManagerAndroid
import org.koin.dsl.module

actual val platformModule = module {
    single<LanguageManager> { LanguageManagerAndroid() }
}