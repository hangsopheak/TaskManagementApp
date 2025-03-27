package com.example.taskmanagement.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taskmanagement.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks WHERE is_synced = 0")
    List<Task> getUnsyncedTasks();

    @Insert
    void insertTask(Task task);

    @Query("UPDATE tasks SET is_synced = 1 WHERE id = :id")
    void markTaskAsSynced(String id);
}

