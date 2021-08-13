package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TASK_TITLE = "task_title";
    public static final String TASK_BODY = "task_body";
    public static final String TASK_STATE = "task_state";
    private List<TaskItem> tasksList;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView TaskRecyclerView = findViewById(R.id.listTask);


        TaskItem task1 = new TaskItem("Task1", " first task here", "in progress");
        TaskItem task2 = new TaskItem("Task2", " second task here", "new");
        TaskItem task3 = new TaskItem("Task3", " third task here", "completed");
        TaskItem task4 = new TaskItem("Task4", " fourth task here", "assigned");


        tasksList = new ArrayList<>();
        tasksList.add(task1);
        tasksList.add(task2);
        tasksList.add(task3);
        tasksList.add(task4);

        adapter = new TaskAdapter(tasksList, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra(TASK_TITLE, tasksList.get(position).getTitle());
                goToDetailsIntent.putExtra(TASK_BODY, tasksList.get(position).getBody());
                goToDetailsIntent.putExtra(TASK_STATE, tasksList.get(position).getState());
                startActivity(goToDetailsIntent);

            }


        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        TaskRecyclerView.setLayoutManager(linearLayoutManager);
        TaskRecyclerView.setAdapter(adapter);
    }

    public void allTasks(View view) {
        Intent showAllTasks=new Intent(MainActivity.this,AllTask.class);
        startActivity(showAllTasks);
    }

    public void addTask(View view) {
        Intent addTaskPage=new Intent(MainActivity.this,AddTask.class);
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
        Intent couseDetail = new Intent(this,TaskDetail.class);
        couseDetail.putExtra("title", "javascript");
        startActivity(couseDetail);
    }

    public void clickbutton5(View view) {
        Intent couseDetail = new Intent(this,TaskDetail.class);
        couseDetail.putExtra("title", "c++");
        startActivity(couseDetail);

    }

    public void clickbutton4(View view) {
        Intent couseDetail = new Intent(this,TaskDetail.class);
        couseDetail.putExtra("title", "java");
        startActivity(couseDetail);
    }

    @Override
    public void onResume() {

        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView address = findViewById(R.id.textView);
        address.setText(preferences.getString("nameKey", "") + "'s Task");
    }
}