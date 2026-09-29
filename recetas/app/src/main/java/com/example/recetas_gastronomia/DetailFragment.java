package com.example.recetas_gastronomia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import retrofit2.Call;
import com.example.recetas_gastronomia.databinding.FragmentDetailBinding;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding binding;
    private CatalogViewModel model;
    private Call<?> pending;

    @Override public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        model = new ViewModelProvider(requireActivity()).get(CatalogViewModel.class);
        if (state == null && getArguments() != null) {
            String id = getArguments().getString("id", "");
            if (!id.isEmpty()) model.selectId(id);
        }
        binding.idInput.setText(model.selectedId);
        setContentVisible(false);
        model.detail.observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;
            setContentVisible(true);
            binding.title.setText(data.title);
            binding.subtitle.setText(data.subtitle);
            binding.description.setText(data.description);
            ImageLoader.load(binding.photo, data.imageUrl);
            binding.status.setText("");
        });
        binding.searchButton.setOnClickListener(v -> {
            String id = String.valueOf(binding.idInput.getText()).trim();
            binding.idLayout.setError(null);
            if (!id.matches("[1-9][0-9]*")) {
                binding.idLayout.setError(getString(R.string.lab_invalid_id));
                return;
            }
            model.selectId(id);
            load(id);
        });
        if (!model.selectedId.isEmpty() && model.detail.getValue() == null) {
            load(model.selectedId);
        }
    }

    private void load(String id) {
        if (pending != null) pending.cancel();
        model.detail.setValue(null);
        setContentVisible(false);
        binding.progress.setVisibility(View.VISIBLE);
        binding.status.setText(R.string.lab_loading);
        pending = CatalogRepository.detail(id, new CatalogRepository.Result<DetailData>() {
            @Override public void success(DetailData data) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                if (data == null) {
                    model.detail.setValue(null);
                    binding.status.setText(R.string.lab_no_detail);
                } else {
                    model.detail.setValue(data);
                }
            }
            @Override public void error(String message) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                binding.status.setText(message);
            }
        });
    }

    private void setContentVisible(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        binding.photo.setVisibility(visibility);
        binding.title.setVisibility(visibility);
        binding.subtitle.setVisibility(visibility);
        binding.description.setVisibility(visibility);
    }

    @Override public void onDestroyView() {
        if (pending != null) pending.cancel();
        binding.photo.setTag(null);
        binding = null;
        super.onDestroyView();
    }
}
