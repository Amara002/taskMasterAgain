package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }

    public void addTask(View view) {
        Toast buttonToast=Toast.makeText(this,"submitted!",Toast.LENGTH_SHORT);
        buttonToast.show();
    }
}