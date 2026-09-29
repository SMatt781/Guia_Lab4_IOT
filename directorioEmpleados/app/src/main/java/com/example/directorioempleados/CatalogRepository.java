package com.example.directorioempleados;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Semana 4: Retrofit + Gson + enqueue. Adaptación de respuestas al diseño de filas.
public final class CatalogRepository {
    public interface Result<T> {
        void success(T value);
        void error(String message);
    }
    private interface Mapper<A, B> { B map(A response); }
    private static final ApiService API = new Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService.class);

    private CatalogRepository() {}

    private static <A, B> Call<A> request(Call<A> call, Mapper<A, B> mapper, Result<B> result) {
        call.enqueue(new Callback<A>() {
            @Override public void onResponse(Call<A> current, Response<A> response) {
                if (current.isCanceled()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    result.error("El servicio respondió HTTP " + response.code()
                            + ". Revisa el ID o vuelve a intentar.");
                    return;
                }
                try {
                    result.success(mapper.map(response.body()));
                } catch (RuntimeException exception) {
                    result.error("La respuesta no tiene el formato esperado.");
                }
            }
            @Override public void onFailure(Call<A> current, Throwable error) {
                if (!current.isCanceled()) {
                    result.error("No se pudo consultar el servicio. Comprueba Internet y vuelve a intentar.");
                }
            }
        });
        return call;
    }

    private static String value(String text) {
        return text == null || text.trim().isEmpty() ? "No informado" : text;
    }

    public static Call<?> groups(Result<List<CardItem>> result) {
        // No se inventa una API de departamentos: se agrupan usuarios externos.
        return request(API.all(0), response -> {
            Map<String, Integer> counts = new TreeMap<>();
            Map<String, String> images = new HashMap<>();
            if (response.users != null) for (ApiModels.Employee employee : response.users) {
                if (employee.company == null || employee.company.department == null) continue;
                String department = employee.company.department;
                counts.put(department, counts.getOrDefault(department, 0) + 1);
                images.putIfAbsent(department, employee.image);
            }
            List<CardItem> rows = new ArrayList<>();
            for (String department : counts.keySet()) {
                rows.add(new CardItem(department, department,
                        counts.get(department) + " empleados", images.get(department)));
            }
            return rows;
        }, result);
    }

    public static Call<?> items(String category, String query, Result<List<CardItem>> result) {
        Call<ApiModels.UsersResponse> call = !query.isEmpty() ? API.search(query, 0)
                : !category.isEmpty() ? API.byDepartment("company.department", category, 0) : API.all(0);
        return request(call, response -> {
            List<CardItem> rows = new ArrayList<>();
            if (response.users != null) for (ApiModels.Employee employee : response.users) {
                String department = employee.company == null ? "Sin departamento"
                        : value(employee.company.department);
                rows.add(new CardItem(String.valueOf(employee.id),
                        value(employee.firstName) + " " + value(employee.lastName),
                        department + "\n" + value(employee.email) + " · ID: " + employee.id, employee.image));
            }
            return rows;
        }, result);
    }

    public static Call<?> detail(String id, Result<DetailData> result) {
        return request(API.detail(id), employee -> {
            ApiModels.Company company = employee.company;
            String role = company == null ? "Sin cargo" : value(company.title);
            String organization = company == null ? "Sin empresa" : value(company.name);
            String department = company == null ? "Sin departamento" : value(company.department);
            String city = employee.address == null ? "Sin ciudad" : value(employee.address.city);
            return new DetailData(String.valueOf(employee.id),
                    value(employee.firstName) + " " + value(employee.lastName),
                    role + "\n" + organization,
                    "Departamento: " + department + "\nCorreo: " + value(employee.email)
                            + "\nTeléfono: " + value(employee.phone) + "\nCiudad: " + city
                            + "\nEdad: " + employee.age + "\n\nDatos ficticios de práctica.",
                    employee.image);
        }, result);
    }

}
