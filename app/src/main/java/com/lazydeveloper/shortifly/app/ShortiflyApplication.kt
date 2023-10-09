package com.lazydeveloper.shortifly.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShortiflyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.e("ShortiflyApplication", "onCreate: ")
    }
}