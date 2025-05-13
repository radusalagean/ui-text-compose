package com.radusalagean.uitextcompose.multiplatform.sample

import android.app.Application

class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application()
    }
}