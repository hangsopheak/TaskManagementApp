package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ITaskService {
    @GET("tasks?_expand=category")
    Call<List<Task>> getTasks();

    @GET("tasks/{id}")
    Call<Task> getTask(@Path("id") int id);

    @POST("tasks")
    Call<Task> createTask(@Body Task task);
}
