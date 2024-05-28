package com.example.testeanalyticaandroid.presentation.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.testeanalyticaandroid.R;

public class MainActivity extends AppCompatActivity {

    private TelemetryViewModel viewModel;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);

        viewModel = new ViewModelProvider(this).get(TelemetryViewModel.class);
        viewModel.getStatus().observe(this, status -> statusTextView.setText(status));
    }
}