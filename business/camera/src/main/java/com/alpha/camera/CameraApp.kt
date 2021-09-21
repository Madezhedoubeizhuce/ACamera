package com.alpha.camera

import android.content.res.Configuration
import android.util.Log
import com.alpha.baselib.application.AppComponent

class CameraApp : AppComponent {
    companion object {
        private const val TAG = "CameraApp"
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "onConfigurationChanged: ")
    }

    override fun onLowMemory() {
        Log.d(TAG, "onLowMemory: ")
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "onTrimMemory: ")
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
    }

    override fun onTerminate() {
        Log.d(TAG, "onTerminate: ")
    }
}