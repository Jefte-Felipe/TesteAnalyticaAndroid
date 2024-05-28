package com.example.testeanalyticaandroid.data.source.remote;

import com.example.testeanalyticaandroid.data.model.OperationResponse;
import com.example.testeanalyticaandroid.data.model.TelemetryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TelemetryService {

    @GET("/telemetry")
    Call<TelemetryResponse> getTelemetry(
            @Query("counter") Integer counter
    );

    @POST("/31/in-operation")
    Call<OperationResponse> sendInOperation(
            @Query("counter") Integer counter
    );
}
