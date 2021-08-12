package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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