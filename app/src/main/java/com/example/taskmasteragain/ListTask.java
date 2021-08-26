package com.example.taskmasteragain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmasteragain.data.TaskDataManger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ListTask extends AppCompatActivity {
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";
    private static final String TAG = "Task";

    private TaskAdapter adapter;
    private List<TaskItem> taskItemList;
//    private final List<TaskItem> data = TaskDataManger.getInstance().getData();

    private TaskDao taskDao;
    private TaskDatabase db;

    private Handler handler;
    private RecyclerView TaskRecyclerView;


//    public List<TaskItem> getTasksList() {
//        return tasksList;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
//        RecyclerView TaskRecyclerView = findViewById(R.id.list2);
        TaskRecyclerView = findViewById(R.id.list2);


        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message message) {
//                        TaskRecyclerView.getAdapter().notifyDataSetChanged();
                Objects.requireNonNull(TaskRecyclerView.getAdapter()).notifyDataSetChanged();
                return false;
            }
        });

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


//        db = Room.databaseBuilder(getApplicationContext(),
//                TaskDatabase.class, AddTask.TASK_LIST).allowMainThreadQueries().build();
//
//        // can be pulled from the network or a local database
//        taskDao = db.taskDao();
//        tasksList = taskDao.findAll();

        taskItemList = new ArrayList<>();
        getTaskDataFromAPI();
        adapter = new TaskAdapter(taskItemList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, taskItemList.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, taskItemList.get(position).getBody());
                goToDetailsIntent.putExtra(TASK_STATE, taskItemList.get(position).getState());
                startActivity(goToDetailsIntent);

            }


            @Override
            public void onDeleteItem(int position) {
//                List<TaskItem> taskItemLists = new ArrayList<>();
                List<Task> tasks = new ArrayList<>();
                Amplify.API.mutate(ModelMutation.delete(tasks.get(position)),
                        response -> Log.i(TAG, "Deleted successfully"),
                        error -> Log.e(TAG, "Delete failed", error)
                );
                Amplify.DataStore.delete(tasks.get(position),
                        success -> Log.i(TAG, "Deleted successfully" + success.item().toString()),
                        failure -> Log.e(TAG, "Delete failed", failure));

                tasks.remove(position);
                Log.i(TAG, "onDeleteItem: our list =>>>> " + tasks.toString());
//                notifyDatasetChanged();
                //                taskDao.delete(taskItemList.get(position));
//                taskItemList.remove(position);
//
////                dataSetChanged();
////                Toast.makeText(ListTask.this, "Item deleted", Toast.LENGTH_SHORT).show();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Item deleted",
                        Toast.LENGTH_LONG);
                toast.show();
                notifyDataSetChanged();

            }


        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        TaskRecyclerView.setLayoutManager(linearLayoutManager);
        TaskRecyclerView.setAdapter(adapter);
    }

    //
//    private void notifyDatasetChanged() {
//        adapter.notifyDataSetChanged();
//    }

    //
//    private void dataSetChanged() {
//        adapter.notifyDataSetChanged();
//    }
    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    private void getTaskDataFromAPI() {
        List<TaskItem> taskItemLists = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Task.class),
                response -> {
                    for (Task task : response.getData()) {
                        taskItemList.add(new TaskItem(task.getTitle(), task.getBody(), task.getState()));
                        Log.i(TAG, "onCreate: the tasks are => " + task.getTitle());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> {
                    Log.e(TAG, "onCreate: Failed to get tasks => " + error.toString());
                    taskItemList = showTasksSavedInDataBase();
                    handler.sendEmptyMessage(1);
                });
    }

    private List<TaskItem> showTasksSavedInDataBase() {
        TaskDatabase taskDatabase = Room.databaseBuilder(this, TaskDatabase.class, "tasks")
                .allowMainThreadQueries().build();
        TaskDao taskDao = taskDatabase.taskDao();
        return taskDao.findAll();

    }

//    public Task deleteTaskToAPI(Task task) {
//        Amplify.API.mutate(ModelMutation.delete(task),
//                success -> Log.i(TAG, "Saved item: " + task.getTitle()),
//                error -> Log.e(TAG, "Could not save item to API/dynamodb" + task.getTitle()));
//        return task;
//
//    }


}