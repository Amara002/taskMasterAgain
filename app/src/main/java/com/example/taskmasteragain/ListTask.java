package com.example.taskmasteragain;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListTask extends AppCompatActivity {
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";
    private List<TaskItem> tasksList;
    private TaskAdapter adapter;

    private TaskDao taskDao;
    private TaskDatabase db;

    public List<TaskItem> getTasksList() {
        return tasksList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        RecyclerView TaskRecyclerView = findViewById(R.id.list2);

//
//        TaskItem task1 = new TaskItem("Task1", " first task ", "in progress");
//        TaskItem task2 = new TaskItem("Task2", " second task ", "new");
//        TaskItem task3 = new TaskItem("Task3", " third task ", "completed");
//        TaskItem task4 = new TaskItem("Task4", " fourth task ", "assigned");
//
//        tasksList = new ArrayList<>();
//        tasksList.add(task1);
//        tasksList.add(task2);
//        tasksList.add(task3);
//        tasksList.add(task4);
        db = Room.databaseBuilder(getApplicationContext(),
                TaskDatabase.class, AddTask.TASK_LIST).allowMainThreadQueries().build();

        // can be pulled from the network or a local database
        taskDao = db.taskDao();
        tasksList = taskDao.findAll();


        adapter = new TaskAdapter(tasksList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, tasksList.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, tasksList.get(position).getBody());
                goToDetailsIntent.putExtra(TASK_STATE, tasksList.get(position).getState());
                startActivity(goToDetailsIntent);

            }

            @Override
            public void onDeleteItem(int position) {
                taskDao.delete(tasksList.get(position));
                tasksList.remove(position);
                notifyDatasetChanged();
                Toast.makeText(ListTask.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }


        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        TaskRecyclerView.setLayoutManager(linearLayoutManager);
        TaskRecyclerView.setAdapter(adapter);
    }

    private void notifyDatasetChanged() {
        adapter.notifyDataSetChanged();
    }

}