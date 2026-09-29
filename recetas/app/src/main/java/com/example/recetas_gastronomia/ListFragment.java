package com.example.recetas_gastronomia;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import retrofit2.Call;
import com.example.recetas_gastronomia.databinding.FragmentListBinding;

public class ListFragment extends Fragment implements SensorEventListener {
    private FragmentListBinding binding;
    private CatalogViewModel model;
    private CardAdapter adapter;
    private Call<?> pending;
    private SensorManager sensorManager;
    private Sensor sensor;
    private final float[] gravity = new float[3];
    private boolean initialized;
    private long lastShake;
    private Call<?> randomCall;

    @Override public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        model = new ViewModelProvider(requireActivity()).get(CatalogViewModel.class);
        if (state == null && getArguments() != null) {
            String category = getArguments().getString("category", "");
            if (!category.isEmpty()) model.changeFilter(category, "");
        }
        binding.searchInput.setText(model.query);
        adapter = new CardAdapter(item -> {
            model.selectId(item.id);
            Bundle args = new Bundle();
            args.putString("id", item.id);
            androidx.navigation.Navigation.findNavController(view)
                    .navigate(R.id.action_list_to_detail, args);
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);
        model.items.observe(getViewLifecycleOwner(), rows -> {
            if (rows == null) return;
            adapter.submit(rows);
            String filter = !model.query.isEmpty() ? model.query : model.category;
            binding.status.setText(rows.isEmpty() ? getString(R.string.lab_empty)
                    : getString(R.string.lab_items_count, rows.size())
                    + (filter.isEmpty() ? "" : " · " + getString(R.string.lab_filter, filter)));
        });
        binding.searchButton.setOnClickListener(v -> {
            String query = String.valueOf(binding.searchInput.getText()).trim();
            binding.searchLayout.setError(null);
            if (query.isEmpty()) {
                binding.searchLayout.setError(getString(R.string.lab_invalid_search));
                return;
            }
            model.changeFilter("", query);
            load();
        });
        binding.allButton.setOnClickListener(v -> {
            binding.searchLayout.setError(null);
            binding.searchInput.setText("");
            model.changeFilter("", "");
            load();
        });
        binding.sensorTestButton.setOnClickListener(v -> { requestSurprise(); });
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (model.items.getValue() == null) load();
    }

    private void load() {
        if (pending != null) pending.cancel();
        binding.progress.setVisibility(View.VISIBLE);
        binding.status.setText(R.string.lab_loading);
        // Vaciar filas previas evita mostrar resultados del filtro anterior durante la consulta.
        adapter.submit(java.util.Collections.emptyList());
        pending = CatalogRepository.items(model.category, model.query,
                new CatalogRepository.Result<List<CardItem>>() {
            @Override public void success(List<CardItem> rows) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                model.items.setValue(rows);
            }
            @Override public void error(String message) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                binding.status.setText(message);
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        if (sensor == null) {
            binding.sensorStatus.setText(R.string.lab_sensor_missing);
        } else if (sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)) {
            binding.sensorStatus.setText(getString(R.string.lab_sensor_ready, sensor.getName()));
        } else {
            binding.sensorStatus.setText(R.string.lab_sensor_missing);
        }
        initialized = false;
    }

    @Override public void onPause() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        if (randomCall != null) { randomCall.cancel(); randomCall = null; }
        if (binding != null) binding.sensorTestButton.setEnabled(true);
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (binding == null || !isResumed() || event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        if (!initialized) {
            System.arraycopy(event.values, 0, gravity, 0, 3);
            initialized = true;
            return;
        }
        float squared = 0;
        for (int i = 0; i < 3; i++) {
            gravity[i] = 0.8f * gravity[i] + 0.2f * event.values[i];
            float linear = event.values[i] - gravity[i];
            squared += linear * linear;
        }
        binding.sensorStatus.setText(getString(R.string.lab_shake_values,
                event.values[0], event.values[1], event.values[2]));
        long now = SystemClock.elapsedRealtime();
        if (Math.sqrt(squared) > 4.0 && now - lastShake > 2000 && randomCall == null) {
            lastShake = now;
            requestSurprise();
        }
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void requestSurprise() {
        if (randomCall != null) return;
        binding.sensorStatus.setText(R.string.lab_shake_detected);
        binding.sensorTestButton.setEnabled(false);
        randomCall = CatalogRepository.random(new CatalogRepository.Result<DetailData>() {
            @Override public void success(DetailData data) {
                randomCall = null;
                if (binding == null || !isResumed()) return;
                binding.sensorTestButton.setEnabled(true);
                if (data == null) {
                    binding.sensorStatus.setText(R.string.lab_no_detail);
                    return;
                }
                model.selectId(data.id);
                model.detail.setValue(data);
                Bundle args = new Bundle();
                args.putString("id", data.id);
                androidx.navigation.Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_list_to_detail, args);
            }
            @Override public void error(String message) {
                randomCall = null;
                if (binding == null) return;
                binding.sensorTestButton.setEnabled(true);
                binding.sensorStatus.setText(message);
            }
        });
    }

    @Override public void onDestroyView() {
        if (pending != null) pending.cancel();
        if (randomCall != null) randomCall.cancel();
        binding.recycler.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }
}
