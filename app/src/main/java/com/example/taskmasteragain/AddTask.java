package com.example.taskmasteragain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.taskmasteragain.data.TaskDataManger;

import java.util.HashMap;

public class AddTask extends AppCompatActivity {

    public static final String TASK_LIST = "task-list";
    private static final String TAG = "AddTask";
    private TaskDao taskDao;
    private TaskDatabase database;
    private int taskItemImage;

    private static HashMap<String, Integer> imageIconDatabase = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

//        configureAmplify();

        imageIconDatabase.put("Task1", R.drawable.ic_tasks);
        imageIconDatabase.put("Task2", R.drawable.ic_list);
        imageIconDatabase.put("Task3", R.drawable.ic_to_do_list);

        database = Room.databaseBuilder(getApplicationContext(), TaskDatabase.class, TASK_LIST)
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        Spinner spinner = findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tasks, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String text = (String) adapterView.getItemAtPosition(position);

                taskItemImage = imageIconDatabase.get(text);
                Log.i(TAG, "onItemSelected: " + text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button addButton = findViewById(R.id.addbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputTitle = findViewById(R.id.editItemTitle);
                EditText inputBody = findViewById(R.id.editTextBody);
                EditText inputState = findViewById(R.id.editTextState);

                String title = inputTitle.getText().toString();
                String body = inputBody.getText().toString();
                String state = inputState.getText().toString();

//                create Task Item
                Task task = Task.builder()
                        .title(title)
                        .body(body)
                        .state(state)
                        .build();

                if (isNetworkAvailable(getApplicationContext())) {
                    Log.i(TAG, "onClick: the network is available");
                } else {
                    Log.i(TAG, "onClick: net down");
                }

                saveTaskToAPI(task);
                TaskDataManger.getInstance().getData().add(new TaskItem(task.getTitle() , task.getBody(),task.getState()));
                Toast.makeText(AddTask.this, "Task saved", Toast.LENGTH_SHORT).show();


                // save data
                TaskItem taskItem = new TaskItem(title, body, state);
                taskItem.setImage(taskItemImage);
                taskDao.insertOne(taskItem);
//                Toast buttonToast=Toast.makeText(AddTask.this,"submitted!",Toast.LENGTH_SHORT);
//                buttonToast.show();
                Intent addTaskPage=new Intent(AddTask.this,ListTask.class);
                startActivity(addTaskPage);


            }
        });

    }

//    private void configureAmplify() {
//        // configure Amplify plugins
//        try {
//            Amplify.addPlugin(new AWSDataStorePlugin());
//            Amplify.addPlugin(new AWSApiPlugin());
//            Amplify.configure(getApplicationContext());
//            Log.i(TAG, "onCreate: Successfully initialized Amplify plugins");
//        } catch (AmplifyException exception) {
//            Log.e(TAG, "onCreate: Failed to initialize Amplify plugins => " + exception.toString());
//        }
//    }

    public Task saveTaskToAPI(Task task) {
        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i(TAG, "Saved item: " + task.getTitle()),
                error -> Log.e(TAG, "Could not save item to API/dynamodb" + task.getTitle()));
        return task;

    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().isConnected();
    }

}