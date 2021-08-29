package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Spinner teamsList = findViewById(R.id.spinnerTeam1);
        String[] teams = new String[]{"", "Team 1", "Team 2", "Team 3"};
        ArrayAdapter<String> TeamsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teams);
        teamsList.setAdapter(TeamsAdapter);

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();


        // save the user name in sharedPreference
        findViewById(R.id.button7).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.editTextTextPersonName3)).getText().toString();


            Spinner teamSpinner = (Spinner) findViewById(R.id.spinnerTeam1);
            String teamName = teamSpinner.getSelectedItem().toString();

            preferenceEditor.putString("username", username);
            preferenceEditor.putString("teamName", teamName);
            preferenceEditor.apply();

            Toast toast = Toast.makeText(this, "saved!", Toast.LENGTH_LONG);
            toast.show();

            Intent mainIntent = new Intent(Setting.this, MainActivity.class);
            startActivity(mainIntent);
        });
    }
//    public void saveName(View view) {
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);// getter
//        SharedPreferences.Editor preferenceEditor = preferences.edit();
//        EditText address = findViewById(R.id.editTextTextPersonName3);
//        preferenceEditor.putString("nameKey", address.getText().toString());
//        preferenceEditor.apply();
//
//        Toast toast = Toast.makeText(this, "Name Saved!", Toast.LENGTH_LONG);
//        toast.show();
//        Intent saveTaskName=new Intent(Setting.this,MainActivity.class);
//        startActivity(saveTaskName);
//    }
}