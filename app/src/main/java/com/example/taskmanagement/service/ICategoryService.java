package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Category;
import com.example.taskmanagement.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICategoryService {
    @GET("categories")
    Call<List<Category>> getCategories();

}
