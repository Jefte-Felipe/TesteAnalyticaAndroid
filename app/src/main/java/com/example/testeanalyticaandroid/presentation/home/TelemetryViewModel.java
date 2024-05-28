package com.example.testeanalyticaandroid.presentation.home;

import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.testeanalyticaandroid.data.model.OperationResponse;
import com.example.testeanalyticaandroid.data.model.TelemetryResponse;
import com.example.testeanalyticaandroid.data.source.remote.TelemetryService;
import java.util.HashMap;
import java.util.Map;
import androidx.lifecycle.ViewModelKt;

public class TelemetryViewModel extends ViewModel {
    private final TelemetryService service;

    private final MutableLiveData<String> _status = new MutableLiveData<>();
    public LiveData<String> getStatus() {
        return _status;
    }

    private int counter = 0;

    private final Map<String, Object> sensorData = new HashMap<>();

    public TelemetryViewModel(TelemetryService service) {
        this.service = service;
        fetchTelemetry();
    }

    private void fetchTelemetry() {
        ViewModelKt.getViewModelScope().launch(() -> {
            try {
                TelemetryResponse response = service.getTelemetry(counter,0);

                // Save the received data until the combination of the following sensors is detected
                sensorData.put(response.getSensor(), response.getCurrentValue());

                // Starts at 0 and increments 1 for each getTelemetry request
                counter++;
                boolean shouldCallAgain = checkOperationStatus();
                if (shouldCallAgain) {
                    fetchTelemetry();
                }
            } catch (Exception e) {
                _status.postValue("Error fetching telemetry");
            }
        });
    }

    private boolean checkOperationStatus() {
        Double speed = (Double) sensorData.getOrDefault("speed", 0.0);
        Boolean engineStatus = (Boolean) sensorData.getOrDefault("engine_status", false);
        Boolean caneStatus = (Boolean) sensorData.getOrDefault("sugar_cane_elevator_status", false);
        Boolean industryStatus = (Boolean) sensorData.getOrDefault("industry_status", false);

        if (speed > 0.0 && engineStatus && caneStatus && industryStatus) {
            _status.postValue("In operation");
            sendInOperation();
            return false;
        } else {
            _status.postValue("Indeterminate event");
            return true;
        }
    }

    private void sendInOperation() {
        ViewModelKt.getViewModelScope().launch(() -> {
            try {
             OperationResponse response = service.sendInOperation(counter,0);
                Log.d("Telemetry", response.getMessage());
            } catch (Exception e) {
                Log.e("Telemetry", "Error sending operation");
            }
        });
    }
}