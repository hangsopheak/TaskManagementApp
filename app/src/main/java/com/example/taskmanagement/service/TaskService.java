package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TaskService {
    @GET("tasks?_expand=category&_sort=createdDate&_order=desc")
    Call<List<Task>> getTasks(@Query("_page") int page, @Query("_limit") int limit, @Query("createdBy") String createdBy);

    @GET("tasks/{id}")
    Call<Task> getTask(@Path("id") int id);

    @POST("tasks")
    Call<Task> createTask(@Body Task task);

    @DELETE("tasks/{id}")
    Call<Void> deleteTask(@Path("id") int taskId);
}