package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            Intent allint = new Intent(this, AllTask.class);
            startActivity(allint);
            return true;
        }

        if (id == R.id.addMenu) {
            Intent add1Intent = new Intent(this, AddTask.class);
            startActivity(add1Intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}