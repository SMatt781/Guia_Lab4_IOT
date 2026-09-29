package com.example.catalogoproductos;

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
        return request(API.categories(), response -> {
            List<CardItem> rows = new ArrayList<>();
            for (ApiModels.Category category : response) {
                rows.add(new CardItem(category.slug, category.name,
                        "Consultar productos de esta categoría", null));
            }
            return rows;
        }, result);
    }

    public static Call<?> items(String category, String query, Result<List<CardItem>> result) {
        Call<ApiModels.ProductsResponse> call = !query.isEmpty() ? API.search(query, 0)
                : !category.isEmpty() ? API.byCategory(category, 0) : API.all(0);
        return request(call, response -> {
            List<CardItem> rows = new ArrayList<>();
            if (response.products != null) for (ApiModels.Product product : response.products) {
                rows.add(new CardItem(String.valueOf(product.id), product.title,
                        String.format(Locale.US, "USD %.2f · Stock: %d · ID: %d",
                                product.price, product.stock, product.id), product.thumbnail));
            }
            return rows;
        }, result);
    }

    public static Call<?> detail(String id, Result<DetailData> result) {
        return request(API.detail(id), product -> new DetailData(
                String.valueOf(product.id), product.title,
                String.format(Locale.US, "USD %.2f · Calificación: %.1f", product.price, product.rating),
                "Categoría: " + value(product.category) + "\nMarca: " + value(product.brand)
                        + "\nStock: " + product.stock + "\n\n" + value(product.description),
                product.thumbnail), result);
    }

}
