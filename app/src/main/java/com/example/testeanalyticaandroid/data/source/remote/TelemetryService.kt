package com.example.testeanalyticaandroid.data.source.remote

import com.example.testeanalyticaandroid.data.model.OperationResponse
import com.example.testeanalyticaandroid.data.model.TelemetryResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TelemetryService {
    @GET("/telemetry")
    suspend fun getTelemetry(@Query("counter") counter: Int): TelemetryResponse

    @POST("/31/in-operation")
    suspend fun sendInOperation(@Query("counter") counter: Int): OperationResponse
}