package com.example.testeanalyticaandroid.presentation.home;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testeanalyticaandroid.app.RetrofitClient;
import com.example.testeanalyticaandroid.data.model.OperationResponse;
import com.example.testeanalyticaandroid.data.model.TelemetryResponse;
import com.example.testeanalyticaandroid.data.source.remote.TelemetryService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TelemetryViewModel extends ViewModel {

    private final MutableLiveData<String> _status = new MutableLiveData<>();

    public LiveData<String> getStatus() {
        return _status;
    }

    private int counter = 0;

    private final Map<String, Object> sensorData = new HashMap<>();

    public TelemetryViewModel() {
        fetchTelemetry();
    }
    private void fetchTelemetry() {
        try {
            RetrofitClient.getClient().create(TelemetryService.class).getTelemetry(counter).enqueue(
                    new Callback<TelemetryResponse>() {
                        @Override
                        public void onResponse(Call<TelemetryResponse> call, Response<TelemetryResponse> response) {
                            if (!response.isSuccessful()) {
                                onFailure(call, new RuntimeException());
                                return;
                            }

                            TelemetryResponse body = response.body();

                            Log.e(TelemetryViewModel.class.getSimpleName(), body.toString());
                            // Save the received data until the combination of the following sensors is detected
                            sensorData.put(body.getSensor(), body.getCurrentValue());

                            // Starts at 0 and increments 1 for each getTelemetry request
                            counter++;

                            boolean shouldCallAgain = checkOperationStatus();
                            if (shouldCallAgain) {
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        fetchTelemetry();
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void onFailure(Call<TelemetryResponse> call, Throwable t) {
                            _status.postValue("Erro ao buscar telemetria");
                        }
                    }
            );

        } catch (Exception e) {
            _status.postValue("Erro ao buscar telemetria");
        }
    }

    private boolean checkOperationStatus() {
        Double speed = (Double) sensorData.getOrDefault("speed", 0.0);
        Boolean engineStatus = (Boolean) sensorData.getOrDefault("engine_status", false);
        Boolean caneStatus = (Boolean) sensorData.getOrDefault("sugar_cane_elevator_status", false);
        Boolean industryStatus = (Boolean) sensorData.getOrDefault("industry_status", false);

        if (speed > 0.0 && engineStatus && caneStatus && industryStatus) {
            _status.postValue("Em operação");
            sendInOperation();
            return false;
        } else {
            _status.postValue("Evento indeterminado");
            return true;
        }
    }

    private void sendInOperation() {
        try {
            Log.e(TelemetryViewModel.class.getSimpleName(), String.valueOf(counter));
            RetrofitClient.getClient().create(TelemetryService.class).sendInOperation(counter).enqueue(
                    new Callback<OperationResponse>() {
                        @Override
                        public void onResponse(Call<OperationResponse> call, Response<OperationResponse> response) {
                            if (!response.isSuccessful()) {
                                onFailure(call, new RuntimeException());
                                return;
                            }

                            Log.e(TelemetryViewModel.class.getSimpleName(), response.message());

                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fetchTelemetry();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onFailure(Call<OperationResponse> call, Throwable t) {
                            Log.e("Telemetry", "Erro ao enviar operação");
                        }
                    }
            );
        } catch (Exception e) {
            Log.e("Telemetry", "Erro ao enviar operação");
        }
    }
}