package com.example.recetas_gastronomia;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.recetas_gastronomia.databinding.ActivityMainBinding;

// Ejemplo de práctica elaborado con asistencia de IA.
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiInsets.apply(binding.getRoot());
        binding.enterButton.setOnClickListener(v -> enter());
        if (!NetworkUtils.hasInternet(this)) showNetworkDialog();
    }

    private void enter() {
        if (NetworkUtils.hasInternet(this)) {
            startActivity(new Intent(this, AppActivity.class));
        } else {
            showNetworkDialog();
        }
    }

    private void showNetworkDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.lab_network_title)
                .setMessage(R.string.lab_network_message)
                .setPositiveButton(R.string.lab_settings, (dialog, which) ->
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                .setNegativeButton(R.string.lab_cancel, null)
                .show();
    }
}
