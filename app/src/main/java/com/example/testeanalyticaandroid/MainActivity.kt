package com.example.testeanalyticaandroid

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TelemetryViewModel
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.statusTextView)
        viewModel = ViewModelProvider(this).get(TelemetryViewModel::class.java)

        viewModel.status.observe(this, Observer { status ->
            statusTextView.text = status
        })
    }
}