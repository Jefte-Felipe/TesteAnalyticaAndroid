package com.example.testeanalyticaandroid.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testeanalyticaandroid.data.source.remote.TelemetryService
import kotlinx.coroutines.launch

class TelemetryViewModel(
    private val service: TelemetryService,
) : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private var counter = 0

    private val sensorData = mutableMapOf<String, Any>()

    init {
        fetchTelemetry()
    }

    private fun fetchTelemetry() {
        viewModelScope.launch {
            try {
                val response = service.getTelemetry(counter)

                //Salva os dados recebidos até que a combinação dos seguintes sensores seja detectada
                sensorData[response.sensor] = response.currentValue

                //Começa em 0 e incrementa 1 a cada request de getTelemetry
                counter++
                val shoulCallAgain = checkOperationStatus()
                if (shoulCallAgain) {
                    fetchTelemetry()
                }
            } catch (e: Exception) {
                _status.postValue("Erro ao buscar telemetria")
            }
        }
    }

    private fun checkOperationStatus(): Boolean {
        val speed = sensorData["speed"] as? Double ?: 0.0
        val engineStatus = sensorData["engine_status"] as? Boolean ?: false
        val caneStatus = sensorData["sugar_cane_elevator_status"] as? Boolean ?: false
        val industryStatus = sensorData["industry_status"] as? Boolean ?: false

        return if (speed > 0.0 && engineStatus && caneStatus && industryStatus) {
            _status.postValue("Em operação")
            sendInOperation()
            false
        } else {
            _status.postValue("Evento indeterminado")
            true
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
