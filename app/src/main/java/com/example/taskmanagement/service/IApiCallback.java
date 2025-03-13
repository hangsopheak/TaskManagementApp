package com.example.taskmanagement.service;

public interface IApiCallback<T> {
    void onSuccess(T result);
    void onError(String errorMessage);

}
