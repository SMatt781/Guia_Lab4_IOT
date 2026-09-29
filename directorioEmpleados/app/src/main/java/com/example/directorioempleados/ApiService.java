package com.example.directorioempleados;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("users") Call<ApiModels.UsersResponse> all(@Query("limit") int limit);
    @GET("users/search") Call<ApiModels.UsersResponse>
            search(@Query("q") String query, @Query("limit") int limit);
    @GET("users/filter") Call<ApiModels.UsersResponse>
            byDepartment(@Query("key") String key, @Query("value") String department,
                    @Query("limit") int limit);
    @GET("users/{id}") Call<ApiModels.Employee> detail(@Path("id") String id);
}
