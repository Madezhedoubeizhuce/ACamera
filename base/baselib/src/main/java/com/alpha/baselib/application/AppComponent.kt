package com.alpha.baselib.application

import android.content.res.Configuration

interface AppComponent {
    fun onConfigurationChanged(newConfig: Configuration)

    fun onLowMemory()

    fun onTrimMemory(level: Int)

    fun onCreate()

    fun onTerminate()
}