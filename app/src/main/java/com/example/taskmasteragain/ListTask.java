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
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmasteragain.data.TaskDataManger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class ListTask extends AppCompatActivity {


    private static final String TAG = "TeamTasks";
    private List<TaskItem> tasks;
    private TaskAdapter adapter;

    private RecyclerView taskRecyclerView;
    private LinearLayoutManager linearLayoutManager;


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        setTitle("Team Tasks");


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                notifyDataSetChanged();
                return false;
            }
        });


    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String teamName = sharedPreferences.getString("teamName", "");

        tasks = new ArrayList<>();

        if (!teamName.equals("")) {
            ((TextView) findViewById(R.id.textView7)).setText(teamName + " Tasks");
            getTeamTasksFromAPI(teamName);

        }



        // RecycleView

        taskRecyclerView = findViewById(R.id.list2);
        adapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), TaskDetail.class);
                goToDetailsIntent.putExtra("Title", tasks.get(position).getTitle());
                goToDetailsIntent.putExtra("Body", tasks.get(position).getBody());
                goToDetailsIntent.putExtra("State", tasks.get(position).getState());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {

                tasks.remove(position);
                listItemDeleted();

            }
        });

        linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);


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

    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

}