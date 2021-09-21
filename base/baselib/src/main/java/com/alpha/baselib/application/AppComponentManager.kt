package com.alpha.baselib.application

import android.content.res.Configuration

object AppComponentManager {
    private val appList = mutableListOf<AppComponent>()

    fun registerApp(app: AppComponent) {
        appList.add(app)
    }

    fun unregisterApp(app: AppComponent) {
        appList.remove(app)
    }

    fun onCreate() {
        for (app in appList) {
            app.onCreate()
        }
    }

    fun onTerminate() {
        for (app in appList) {
            app.onTerminate()
        }
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        for (app in appList) {
            app.onConfigurationChanged(newConfig)
        }
    }

    fun onLowMemory() {
        for (app in appList) {
            app.onLowMemory()
        }
    }

    fun onTrimMemory(level: Int) {
        for (app in appList) {
            app.onTrimMemory(level)
        }
    }
}