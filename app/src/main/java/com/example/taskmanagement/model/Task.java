package com.example.taskmanagement.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    @JsonAdapter(ISO8601DateAdapter.class)
    private Date dueDate;
    @SerializedName("categoryId")
    private String categoryId;
    private boolean isCompleted;
    private String createdBy;
    @JsonAdapter(ISO8601DateAdapter.class)
    private Date createdDate;
    private String updatedBy;
    @JsonAdapter(ISO8601DateAdapter.class)
    private Date updatedDate;

    private Category category;

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getFormattedDueDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(dueDate);
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Task(String title, String description, Date dueDate, String categoryId, boolean isCompleted, String createdBy, Date createdDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.categoryId = categoryId;
        this.isCompleted = isCompleted;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    // Custom Date adapter class
    public static class ISO8601DateAdapter extends TypeAdapter<Date> {
        private final SimpleDateFormat formatter;

        public ISO8601DateAdapter() {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        @Override
        public void write(JsonWriter out, Date date) throws IOException {
            if (date == null) {
                out.nullValue();
            } else {
                out.value(formatter.format(date));
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            try {
                return formatter.parse(in.nextString());
            } catch (Exception e) {
                return null;
            }
        }
    }


}
