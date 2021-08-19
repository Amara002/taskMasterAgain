package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Intent intent = getIntent();

        String title = intent.getExtras().getString(MainActivity.TASK_TITLE);
        TextView titleTextView = findViewById(R.id.taskTitle);
        titleTextView.setText(title);
        String body = intent.getExtras().getString(MainActivity.TASK_BODY);
        TextView bodyTextView = findViewById(R.id.taskBody);
        bodyTextView.setText(body);
        String state = intent.getExtras().getString(MainActivity.TASK_STATE);
        TextView stateTextView = findViewById(R.id.taskState);
        stateTextView.setText(state);


    }
}