package com.radusalagean.uitextcompose.multiplatform.sample.di

import com.radusalagean.uitextcompose.multiplatform.sample.ui.screen.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val commonModule = module {
    viewModelOf(::MainViewModel)
}