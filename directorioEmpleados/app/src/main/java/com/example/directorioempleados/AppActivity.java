package com.example.directorioempleados;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.example.directorioempleados.databinding.ActivityAppBinding;

// Una Activity aloja tres fragmentos; el menú y la barra superior permanecen.
public class AppActivity extends AppCompatActivity {
    private ActivityAppBinding binding;
    private NavController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiInsets.apply(binding.getRoot());

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host);
        controller = host.getNavController();
        CatalogViewModel model = new ViewModelProvider(this).get(CatalogViewModel.class);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int destination = item.getItemId();
            if (controller.getCurrentDestination() != null
                    && controller.getCurrentDestination().getId() == destination) return true;
            if (destination == R.id.listFragment) {
                // Entrada directa por menú: lista sin categoría, con buscador.
                model.changeFilter("", "");
            }
            NavOptions options = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .setLaunchSingleTop(true)
                    .build();
            controller.navigate(destination, null, options);
            return true;
        });
        controller.addOnDestinationChangedListener((nav, destination, arguments) -> {
            // setChecked sincroniza el menú sin disparar otra navegación.
            binding.bottomNav.getMenu().findItem(destination.getId()).setChecked(true);
            binding.toolbar.setTitle(destination.getLabel());
        });
        // El enunciado pasado pide Atrás -> MainActivity, no recorrer las pestañas.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { finish(); }
        });
    }
}
