package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;


import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;



import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;

import com.amplifyframework.datastore.generated.model.Team;


import java.util.ArrayList;
import java.util.List;

public class AddTask extends AppCompatActivity {


    private static final String TAG = "AddTaskActivity";

    private TaskDao taskDao;

    private String teamId = "";

    private final List<Team> teams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("Add Task");
        getAllTeamsDataFromAPI();


        Spinner teamsList = findViewById(R.id.spinnerTeam);
        String[] teams = new String[]{"Team 1", "Team 2", "Team 3"};
        ArrayAdapter<String> TeamsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teams);
        teamsList.setAdapter(TeamsAdapter);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();


        TaskDatabase database = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        // add task button listener
        findViewById(R.id.addbutton).setOnClickListener(view -> {
            String taskTitle = ((EditText) findViewById(R.id.editItemTitle)).getText().toString();
            String taskBody = ((EditText) findViewById(R.id.editTextBody)).getText().toString();
            String taskState = ((EditText) findViewById(R.id.editTextState)).getText().toString();

            Spinner teamSpinner = (Spinner) findViewById(R.id.spinnerTeam);
            String teamName = teamSpinner.getSelectedItem().toString();

            preferenceEditor.putString("teamName", teamName);
            preferenceEditor.apply();


            TaskItem newTask = new TaskItem(taskTitle, taskBody, taskState);
            taskDao.insertOne(newTask);

            Log.i(TAG, "on button Listener the team id is >>>>> " + getTeamId(teamName));


            addTaskToDynamoDB(taskTitle,
                    taskBody,
                    taskState,
                    new Team(getTeamId(teamName), teamName));

        });


    }

    public void addTaskToDynamoDB(String taskTitle, String taskBody, String taskState, Team team) {
        com.amplifyframework.datastore.generated.model.Task task = com.amplifyframework.datastore.generated.model.Task.builder()
                .title(taskTitle)
                .body(taskBody)
                .state(taskState)
                .team(team)
                .build();

        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i(TAG, "Saved item: " + task.getTitle()),
                error -> Log.e(TAG, "Could not save item to API", error));

        Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
        toast.show();

    }

    private void getAllTeamsDataFromAPI() {
        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        teams.add(team);
                        Log.i(TAG, "the team id DynamoDB are => " + team.getTeamName() + "  " + team.getId());
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get team from DynamoDB => " + error.toString())
        );
    }

    public String getTeamId(String teamName) {
        for (Team team : teams) {
            if (team.getTeamName().equals(teamName)) {
                return team.getId();
            }
        }
        return "";
    }

}