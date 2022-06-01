package com.kohuyn.movie

import android.app.Application
import timber.log.Timber
import timber.log.Timber.*

class MovieApp:Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}