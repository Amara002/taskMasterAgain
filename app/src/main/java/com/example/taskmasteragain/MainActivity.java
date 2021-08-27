package com.example.taskmasteragain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";
//    private List<TaskItem> tasksList;
//    private TaskAdapter adapter;


    private TaskDao taskDao;
    private TaskDatabase db;
    private TaskAdapter adapter;
    public static final String TAG = "MainActivity";
    private Handler handler;
    private RecyclerView TaskRecyclerView;
    private List<TaskItem> taskItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView TaskRecyclerView = findViewById(R.id.listTask);
        configureAmplify();

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean handleMessage(@NonNull Message message) {
//                        TaskRecyclerView.getAdapter().notifyDataSetChanged();
                Objects.requireNonNull(TaskRecyclerView.getAdapter()).notifyDataSetChanged();
                return false;
            }
        });


////        lab32
//        TaskItem task1 = new TaskItem("Task1", " first task ", "in progress");
//        TaskItem task2 = new TaskItem("Task2", " second task ", "new");
//        TaskItem task3 = new TaskItem("Task3", " third task ", "completed");
//        TaskItem task4 = new TaskItem("Task4", " fourth task ", "assigned");
//
//
//        tasksList = new ArrayList<>();
//        tasksList.add(task1);
//        tasksList.add(task2);
//        tasksList.add(task3);
//        tasksList.add(task4);
//        db = Room.databaseBuilder(getApplicationContext(),
//                TaskDatabase.class, AddTask.TASK_LIST).allowMainThreadQueries().build();

        // can be pulled from the network or a local database
//          taskDao = db.taskDao();
//          tasksList = taskDao.findAll();
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

//        private void notifyDatasetChanged() {
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

    public void allTasks(View view) {
        Intent showAllTasks = new Intent(MainActivity.this, AllTask.class);
        startActivity(showAllTasks);
    }

    public void addTask(View view) {
        Intent addTaskPage = new Intent(MainActivity.this, AddTask.class);
        startActivity(addTaskPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.allMenu) {
            Intent allint = new Intent(this, Setting.class);
            startActivity(allint);
            return true;
        }

        if (id == R.id.addMenu) {
            Intent add1Intent = new Intent(this, ListTask.class);
            startActivity(add1Intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clicksetting(View view) {
        Intent clicksetting = new Intent(MainActivity.this, Setting.class);
        MainActivity.this.startActivity(clicksetting);
    }

    public void clickbutton6(View view) {
        Intent couseDetail = new Intent(this, TaskDetail.class);
        couseDetail.putExtra("title", "javascript");
        startActivity(couseDetail);
    }

    public void clickbutton5(View view) {
        Intent couseDetail = new Intent(this, TaskDetail.class);
        couseDetail.putExtra("title", "c++");
        startActivity(couseDetail);

    }

    public void clickbutton4(View view) {
        Intent couseDetail = new Intent(this, TaskDetail.class);
        couseDetail.putExtra("title", "java");
        startActivity(couseDetail);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {

        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView address = findViewById(R.id.textView);
        address.setText(preferences.getString("nameKey", "") + "'s Task");
    }

    private void configureAmplify() {
        // configure Amplify plugins
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
        } catch (AmplifyException exception) {
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }
    }
}