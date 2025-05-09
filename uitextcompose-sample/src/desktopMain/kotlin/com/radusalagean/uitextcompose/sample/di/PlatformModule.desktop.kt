package com.radusalagean.uitextcompose.sample.di

import com.radusalagean.uitextcompose.sample.util.LanguageManager
import com.radusalagean.uitextcompose.sample.util.LanguageManagerDesktop
import org.koin.dsl.module

actual val platformModule = module {
    single<LanguageManager> { LanguageManagerDesktop() }
}