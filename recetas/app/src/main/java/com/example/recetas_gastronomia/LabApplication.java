package com.example.recetas_gastronomia;

import android.app.Application;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Semana 4: un único pool de hilos para las imágenes de toda la app.
public class LabApplication extends Application {
    public final ExecutorService executor = Executors.newFixedThreadPool(4);
}
