package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.service.ICategoryService;
import com.example.taskmanagement.util.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private ICategoryService categoryService;

    public CategoryRepository() {
        categoryService = RetrofitClient.getClient().create(ICategoryService.class);
    }

    public void getCategories(final IApiCallback<List<Category>> callback) {
        Call<List<Category>> call = categoryService.getCategories();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }


}
