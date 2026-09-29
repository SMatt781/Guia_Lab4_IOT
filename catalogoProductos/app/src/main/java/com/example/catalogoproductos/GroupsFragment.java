package com.example.catalogoproductos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import retrofit2.Call;
import com.example.catalogoproductos.databinding.FragmentGroupsBinding;

public class GroupsFragment extends Fragment {
    private FragmentGroupsBinding binding;
    private CatalogViewModel model;
    private CardAdapter adapter;
    private Call<?> pending;

    @Override public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        model = new ViewModelProvider(requireActivity()).get(CatalogViewModel.class);
        adapter = new CardAdapter(item -> {
            model.changeFilter(item.id, "");
            Bundle arguments = new Bundle();
            arguments.putString("category", item.id);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_groups_to_list, arguments);
        });
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);
        model.groups.observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                adapter.submit(list);
                binding.status.setText(list.isEmpty() ? getString(R.string.lab_empty)
                        : getString(R.string.lab_items_count, list.size()));
            }
        });
        binding.reloadButton.setOnClickListener(v -> load());
        if (model.groups.getValue() == null) load();
    }

    private void load() {
        if (pending != null) pending.cancel();
        binding.progress.setVisibility(View.VISIBLE);
        binding.status.setText(R.string.lab_loading);
        pending = CatalogRepository.groups(new CatalogRepository.Result<List<CardItem>>() {
            @Override public void success(List<CardItem> items) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                model.groups.setValue(items);
            }
            @Override public void error(String message) {
                if (binding == null) return;
                binding.progress.setVisibility(View.GONE);
                binding.status.setText(message);
            }
        });
    }

    @Override public void onDestroyView() {
        if (pending != null) pending.cancel();
        binding.recycler.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }
}
