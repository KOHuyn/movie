package com.kohuyn.movie

import android.app.Application
import timber.log.Timber
import timber.log.Timber.*

class MovieApp : Application() {

    companion object {
        private var instance: MovieApp? = null
        fun getInstance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}