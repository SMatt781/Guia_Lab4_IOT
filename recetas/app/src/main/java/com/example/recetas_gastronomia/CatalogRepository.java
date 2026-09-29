package com.example.recetas_gastronomia;

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
            .baseUrl("https://www.themealdb.com/api/json/v1/1/")
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
            if (response.categories != null) for (ApiModels.Category category : response.categories) {
                rows.add(new CardItem(category.strCategory, category.strCategory,
                        value(category.strCategoryDescription), category.strCategoryThumb));
            }
            return rows;
        }, result);
    }

    public static Call<?> items(String category, String query, Result<List<CardItem>> result) {
        Call<ApiModels.MealsResponse> call = !query.isEmpty() ? API.byIngredient(query)
                : !category.isEmpty() ? API.byCategory(category) : API.examples("");
        return request(call, response -> {
            List<CardItem> rows = new ArrayList<>();
            if (response.meals != null) for (ApiModels.Meal meal : response.meals) {
                rows.add(new CardItem(meal.idMeal, meal.strMeal,
                        "ID: " + meal.idMeal, meal.strMealThumb));
            }
            return rows;
        }, result);
    }

    public static Call<?> detail(String id, Result<DetailData> result) {
        return request(API.detail(id), response ->
                response.meals == null || response.meals.isEmpty()
                        ? null : toDetail(response.meals.get(0)), result);
    }

    public static Call<?> random(Result<DetailData> result) {
        return request(API.random(), response ->
                response.meals == null || response.meals.isEmpty()
                        ? null : toDetail(response.meals.get(0)), result);
    }

    private static DetailData toDetail(ApiModels.Meal meal) {
        return new DetailData(meal.idMeal, meal.strMeal,
                "Categoría: " + value(meal.strCategory) + "\nOrigen: " + value(meal.strArea),
                "INGREDIENTES\n" + meal.ingredientsText()
                        + "\nPREPARACIÓN\n" + value(meal.strInstructions), meal.strMealThumb);
    }

}
