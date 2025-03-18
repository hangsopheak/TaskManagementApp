package com.example.taskmanagement.model;

import androidx.annotation.NonNull;

import java.util.UUID;

public class Category {
    private String id;
    private String name;
    private String colorCode;

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public Category(String name, String colorCode) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.colorCode = colorCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
