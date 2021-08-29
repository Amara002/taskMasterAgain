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
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private List<TaskItem> tasks;
    private TaskAdapter adapter;

    private TaskDao taskDao;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Task Master App");

        handler = new Handler(message -> {
            notifyDataSetChanged();
            return false;
        });


        configureAmplify();



        TaskDatabase database = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();


        // Add Task Button listener
        findViewById(R.id.button).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTask);
        });

        // all tasks Button listener
        findViewById(R.id.button3).setOnClickListener(view -> {
            Intent goToAllTask = new Intent(MainActivity.this, AllTask.class);
            startActivity(goToAllTask);
        });

        //  settings  Button listener
        findViewById(R.id.button8).setOnClickListener(view -> {
            Intent goToSettings = new Intent(MainActivity.this, Setting.class);
            startActivity(goToSettings);
        });

        //  Team tasks through list task button
        findViewById(R.id.button4).setOnClickListener(view -> {
            Intent goListTask = new Intent(MainActivity.this, ListTask.class);
            startActivity(goListTask);
        });

        // save Teams to API
        saveTeamToApi("Team 1");
        saveTeamToApi("Team 2");
        saveTeamToApi("Team 3");

    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }



//    public void showDetails(String taskName) {
//        Intent intent = new Intent(MainActivity.this, TaskDetail.class);
//        intent.putExtra("taskName", taskName);
//        startActivity(intent);
//    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("username", "");
        String teamName = sharedPreferences.getString("teamName", "");

        if (!username.equals("")) {
            ((TextView) findViewById(R.id.textView)).setText(username + "'s Task");
        }

        tasks = new ArrayList<>();
        if (teamName.equals("")) {
            getTasksDataFromAPI();
        } else {
            ((TextView) findViewById(R.id.textView0)).setText(teamName + " Tasks");
            getTeamTasksFromAPI(teamName);
        }

//        tasks = new ArrayList<>();
//        if (teamName.equals("")) {
//            getTasksDataFromAPI();
//        } else {
//            getTeamTasksFromAPI(teamName);
//        }

        Log.i(TAG, "onResume: tasks " + tasks);

        RecyclerView taskRecyclerView = findViewById(R.id.listTask);
        adapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra("taskTitle", tasks.get(position).getTitle());
                goToDetailsIntent.putExtra("taskBody", tasks.get(position).getBody());
                goToDetailsIntent.putExtra("taskState", tasks.get(position).getState());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {

                taskDao.delete(tasks.get(position));

                tasks.remove(position);
                listItemDeleted();


            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);
    }

    private void configureAmplify() {

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
        } catch (AmplifyException exception) {
            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
        }
    }

    private void getTasksDataFromAPI() {
        Amplify.API.query(ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class),
                response -> {
                    for (com.amplifyframework.datastore.generated.model.Task task : response.getData()) {
                        tasks.add(new TaskItem(task.getTitle(), task.getBody(), task.getState()));
                        Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + task.getTitle());
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    public void saveTeamToApi(String teamName) {
        Team team = Team.builder().teamName(teamName).build();

        Amplify.API.query(ModelQuery.list(Team.class, Team.TEAM_NAME.contains(teamName)),
                response -> {
                    List<Team> teams = (List<Team>) response.getData().getItems();

                    if (teams.isEmpty()) {
                        Amplify.API.mutate(ModelMutation.create(team),
                                success -> Log.i(TAG, "Saved Team => " + team.getTeamName()),
                                error -> Log.e(TAG, "Could not save Team to API => ", error));
                    }
                },
                error -> Log.e(TAG, "Failed to get Team from DynamoDB => " + error.toString())
        );

    }

    private void getTeamTasksFromAPI(String teamName) {
        Amplify.API.query(ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class),
                response -> {
                    for (com.amplifyframework.datastore.generated.model.Task task : response.getData()) {

                        if ((task.getTeam().getTeamName()).equals(teamName)) {
                            tasks.add(new TaskItem(task.getTitle(), task.getBody(), task.getState()));
                            Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + task.getTitle());
                            Log.i(TAG, "onCreate: the team DynamoDB are => " + task.getTeam().getTeamName());
                        }
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Add_Task) {
            Intent addTaskPage=new Intent(MainActivity.this,AddTask.class);
            startActivity(addTaskPage);
            return true;
        }

        if (id == R.id.allMenu) {
            Intent allTaskPage=new Intent(MainActivity.this,ListTask.class);
            startActivity(allTaskPage);
            return true;
        }

        if (id == R.id.settings) {
            Intent settingsPage=new Intent(MainActivity.this,Setting.class);
            startActivity(settingsPage);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}