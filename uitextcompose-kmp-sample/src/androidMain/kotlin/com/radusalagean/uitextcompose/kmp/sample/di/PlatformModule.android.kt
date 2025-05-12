package com.radusalagean.uitextcompose.kmp.sample.di

import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManager
import com.radusalagean.uitextcompose.kmp.sample.util.LanguageManagerAndroid
import org.koin.dsl.module

actual val platformModule = module {
    single<LanguageManager> { LanguageManagerAndroid() }
}