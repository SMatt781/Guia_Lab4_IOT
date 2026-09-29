package com.example.recetas_gastronomia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("categories.php") Call<ApiModels.CategoriesResponse> categories();
    @GET("filter.php") Call<ApiModels.MealsResponse> byCategory(@Query("c") String category);
    @GET("filter.php") Call<ApiModels.MealsResponse> byIngredient(@Query("i") String ingredient);
    @GET("search.php") Call<ApiModels.MealsResponse> examples(@Query("s") String name);
    @GET("lookup.php") Call<ApiModels.MealsResponse> detail(@Query("i") String id);
    @GET("random.php") Call<ApiModels.MealsResponse> random();
}
