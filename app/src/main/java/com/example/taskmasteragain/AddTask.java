package com.example.taskmasteragain;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.FileUtils;
import android.preference.PreferenceManager;
import android.util.Log;


import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.Toast;



import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;

import com.amplifyframework.datastore.generated.model.Team;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddTask extends AppCompatActivity {


    private static final String TAG = "AddTaskActivity";

    private TaskDao taskDao;

    private String teamId = "";

    private final List<Team> teams = new ArrayList<>();

    static String pattern = "yyMMddHHmmssZ";
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private static String FileUploadName= simpleDateFormat.format(new Date());
    private static String fileUploadExtention = null;
    private static File uploadFile = null;

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

//        lab37
        Button uploadFile = findViewById(R.id.upload_bt);
        uploadFile.setOnClickListener(v1 -> getFileFromDevice());


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
                .fileName(FileUploadName +"."+ fileUploadExtention.split("/")[1])
                .build();

        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i(TAG, "Saved item: " + task.getTitle()),
                error -> Log.e(TAG, "Could not save item to API", error));

        Amplify.Storage.uploadFile(
                FileUploadName +"."+ fileUploadExtention.split("/")[1],
                uploadFile,
                success -> {
                    Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey());
                },
                error -> {
                    Log.e(TAG, "uploadFileToS3: failed " + error.toString());
                }
        );

        Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
        toast.show();

    }
// lab37
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            fileUploadExtention = getContentResolver().getType(uri);

            Log.i(TAG, "onActivityResult: gg is " +fileUploadExtention);
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());
            Log.i(TAG, "onActivityResult:  data => " + data.getType());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

        }
    }

    private void getFileFromDevice() {
        Intent upload = new Intent(Intent.ACTION_GET_CONTENT);
        upload.setType("*/*");
        upload = Intent.createChooser(upload, "Choose a File");
        startActivityForResult(upload, 999); // deprecated
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