package com.example.taskmasteragain.data;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmasteragain.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class TaskDataManger {
    private static TaskDataManger instance = null;
    private List<TaskItem> taskItems = new ArrayList<>();

    private TaskDataManger() {
    }

    public static TaskDataManger getInstance() {
        if (instance == null) {
            instance = new TaskDataManger();
        }

        return instance;
    }

    public void setData(List<TaskItem> data) {
        taskItems = data;
    }

    public List<TaskItem> getData() {
        return taskItems;
    }
}
