package com.radusalagean.uitextcompose.sample.di

import org.koin.dsl.module
import org.koin.core.module.dsl.viewModelOf
import com.radusalagean.uitextcompose.sample.ui.screen.MainViewModel

val commonModule = module {
    viewModelOf(::MainViewModel)
}