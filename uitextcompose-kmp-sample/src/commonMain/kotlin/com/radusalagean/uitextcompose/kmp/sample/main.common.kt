package com.radusalagean.uitextcompose.kmp.sample

import com.radusalagean.uitextcompose.kmp.sample.di.appModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin

fun application() {
    Napier.base(DebugAntilog())
    startKoin {
        modules(appModule())
    }
}