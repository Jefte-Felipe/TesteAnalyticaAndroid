package com.example.testeanalyticaandroid

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TelemetryViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private var counter = 0

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://test.analitica.ag")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(TelemetryService::class.java)

    private val sensorData = mutableMapOf<String, Any>()

    init {
        fetchTelemetry()
    }

    private fun fetchTelemetry() {
        viewModelScope.launch {
            try {
                val response = service.getTelemetry(counter)
                sensorData[response.sensor] = response.currentValue
                counter++
                checkOperationStatus()
                fetchTelemetry()
            } catch (e: Exception) {
                _status.postValue("Erro ao buscar telemetria")
            }
        }
    }

    private fun checkOperationStatus() {
        val speed = sensorData["speed"] as? Int ?: 0
        val engineStatus = sensorData["engine_status"] as? Boolean ?: false
        val caneStatus = sensorData["sugar_cane_elevator_status"] as? Boolean ?: false
        val industryStatus = sensorData["industry_status"] as? Boolean ?: false

        if (speed > 0 && engineStatus && caneStatus && industryStatus) {
            _status.postValue("Em operação")
            sendInOperation()
        } else {
            _status.postValue("Evento indeterminado")
        }
    }

    private fun sendInOperation() {
        viewModelScope.launch {
            try {
                val response = service.sendInOperation(counter)
                Log.d("Telemetry", response.message)
            } catch (e: Exception) {
                Log.e("Telemetry", "Erro ao enviar operação")
            }
        }
    }
}
