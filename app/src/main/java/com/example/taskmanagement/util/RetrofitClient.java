package com.example.taskmanagement.util;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static String baseUrl = "https://task-management-db.onrender.com/";
    private static String dbName = "49e74e2c-dde0-49a9-a2e9-bc016aaa2f59";

    // Setters for configuration
    public static void setBaseUrl(String url) {
        baseUrl = url;
        // Reset retrofit instance to rebuild with new URL
        retrofit = null;
    }

    public static void setDbName(String name) {
        dbName = name;
        // Reset retrofit instance to rebuild with new DB name
        retrofit = null;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create header interceptor
            Interceptor headerInterceptor = chain -> {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .header("X-DB-NAME", dbName)
                        .build();
                return chain.proceed(newRequest);
            };

            // Create OkHttp client with interceptors
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)  // Add header interceptor
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}