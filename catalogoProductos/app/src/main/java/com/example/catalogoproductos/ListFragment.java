package com.example.catalogoproductos;

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
import com.example.catalogoproductos.databinding.FragmentListBinding;

public class ListFragment extends Fragment implements SensorEventListener {
    private FragmentListBinding binding;
    private CatalogViewModel model;
    private CardAdapter adapter;
    private Call<?> pending;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean simulatedDark;

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
        binding.sensorTestButton.setOnClickListener(v -> { simulatedDark = !simulatedDark; showLight(simulatedDark ? 10f : 250f); });
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager == null ? null : sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
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
        
    }

    @Override public void onPause() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        
        super.onPause();
    }

    @Override public void onSensorChanged(SensorEvent event) {
        if (binding == null || !isResumed() || event.sensor.getType() != Sensor.TYPE_LIGHT) return;
        showLight(event.values[0]);
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void showLight(float lux) {
        boolean dark = lux < 50f;
        binding.sensorStatus.setText(getString(dark ? R.string.lab_light_dark
                : R.string.lab_light_normal, lux));
        binding.sensorCard.setCardBackgroundColor(Color.parseColor(dark ? "#FFF3CD" : "#EAF0F8"));
    }

    @Override public void onDestroyView() {
        if (pending != null) pending.cancel();
        
        binding.recycler.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }
}
