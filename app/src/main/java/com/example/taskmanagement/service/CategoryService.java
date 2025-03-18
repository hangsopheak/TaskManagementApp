package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
public interface CategoryService {
    @GET("categories")
    Call<List<Category>> getCategories();

}
