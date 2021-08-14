package com.example.taskmasteragain;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TaskItem.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
