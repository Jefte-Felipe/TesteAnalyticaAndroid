package com.example.testeanalyticaandroid.di

import com.example.testeanalyticaandroid.TelemetryViewModel
import com.example.testeanalyticaandroid.service.TelemetryService
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://test.analitica.ag")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<TelemetryService> {
        get<Retrofit>().create(TelemetryService::class.java)
    }

    viewModelOf(::TelemetryViewModel)
}