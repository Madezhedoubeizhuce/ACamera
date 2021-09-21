package com.alpha.acamera

import android.app.Application
import android.content.res.Configuration
import com.alpha.baselib.application.AppComponentManager
import com.alpha.camera.CameraApp

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppComponentManager.registerApp(CameraApp())
        AppComponentManager.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        AppComponentManager.onTerminate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppComponentManager.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        AppComponentManager.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        AppComponentManager.onTrimMemory(level)
    }
}