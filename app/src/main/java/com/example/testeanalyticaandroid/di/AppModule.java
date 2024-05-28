package com.example.testeanalyticaandroid.di;

import com.example.testeanalyticaandroid.data.source.remote.TelemetryService;
import com.example.testeanalyticaandroid.presentation.home.TelemetryViewModel;

import org.koin.core.module.Module;
import org.koin.dsl.ModuleDSLKt;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppModule {
    public static final Module module = ModuleDSLKt.module(
            ModuleDSLKt.single(TelemetryService.class, () -> {
                return new Retrofit.Builder()
                        .baseUrl("https://test.analitica.ag")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(TelemetryService.class);
            }),
            ModuleDSLKt.viewModelOf(TelemetryViewModel.class)
    );
}