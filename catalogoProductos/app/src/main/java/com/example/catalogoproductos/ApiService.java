package com.example.catalogoproductos;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("products/categories") Call<List<ApiModels.Category>> categories();
    @GET("products") Call<ApiModels.ProductsResponse> all(@Query("limit") int limit);
    @GET("products/category/{category}") Call<ApiModels.ProductsResponse>
            byCategory(@Path("category") String category, @Query("limit") int limit);
    @GET("products/search") Call<ApiModels.ProductsResponse>
            search(@Query("q") String query, @Query("limit") int limit);
    @GET("products/{id}") Call<ApiModels.Product> detail(@Path("id") String id);
}
