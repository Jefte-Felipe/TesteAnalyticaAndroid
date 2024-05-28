package com.example.testeanalyticaandroid.app;

import android.app.Application;

import com.example.testeanalyticaandroid.di.AppModule;

import org.koin.android.java.KoinAndroidApplication;
import org.koin.core.KoinApplication;

public class AnalyticaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KoinAndroidApplication.create(this).modules(AppModule.module);
    }
}