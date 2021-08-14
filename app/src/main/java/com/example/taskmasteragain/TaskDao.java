package com.example.taskmasteragain;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insertOne(TaskItem taskItem);

    @Query("SELECT * FROM taskItem WHERE id like :id")
    TaskItem findById(long id);

    @Query("SELECT * FROM TaskItem")
    List<TaskItem> findAll();

    @Delete
    void delete(TaskItem taskItem);
}
