package com.example.testeanalyticaandroid.app

import android.app.Application
import com.example.testeanalyticaandroid.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            loadKoinModules(
                listOf(appModule)
            )
        }
    }
}