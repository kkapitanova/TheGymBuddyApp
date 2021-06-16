package com.example.thegymbuddyapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.thegymbuddyapp.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    String user;
    String userName;
    DatabaseReference usersRef;
    String userID;
    String joinedDate;
    public static String profileName = "";
    public static String dateJoined = "Date Joined: ";
    public static String totalWorkouts = "Total Workouts: ";

    ListView workoutsListView;
    ArrayList<String> workoutsList = new ArrayList<>();
    ArrayList<workoutModel> workoutsListModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workoutsListView = findViewById(R.id.workoutsListView);
        workoutsListModel = new ArrayList<>();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_workouts, R.id.navigation_addworkout, R.id.navigation_profile, R.id.navigation_userguide)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("Workout Notification", "Workout Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        userID = getIntent().getStringExtra("USER_ID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        usersRef = mDatabase.child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                profileName = "";
                dateJoined = "Joined: ";
                totalWorkouts = "Total Workouts: ";
                userName = snapshot.child(userID).child("name").getValue().toString();
                user = snapshot.child(userID).getValue().toString();
                joinedDate = snapshot.child(userID).child("joined").getValue().toString();
                profileName = profileName + userName;
                dateJoined = dateJoined + joinedDate;
                String workouts = snapshot.child(userID).child("workouts").getValue().toString();
                System.out.println(workouts);
                String[] workoutsArray = workouts.split("name=");
                int count = 0;
                System.out.println(workoutsArray.length);
                for (int i = 0; i<workoutsArray.length-1; i++){
                    count =  count + 1;
                }
                System.out.println("Stringvalue");
                System.out.println(String.valueOf(count));
                totalWorkouts = totalWorkouts + String.valueOf(count);



            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        new MainActivity.fetchWorkouts().execute();


    }


    public class fetchWorkouts extends AsyncTask<String, String, ArrayList<String>> {
        @Override
        public void onPreExecute() {
            super .onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
//            System.out.println(userID);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("workouts");

            workoutsListModel.clear();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        workoutsList.add(snapshot.getValue().toString());
                    }

                    for (int i = 0; i < workoutsList.size(); i++) {
                        try {

                            String workout = workoutsList.get(i); //arraylist contains strings

                            String name = "Workout name";
                            int index = workout.indexOf("=");
                            name = workout.substring(0,index);
                            String workoutName = name.replace("{", "");
                            System.out.println("workoutName");
                            System.out.println(workoutName);


                            String[] workoutDescriptionStringArray = workout.split("=");
                            String description = "";
                            for (String el: workoutDescriptionStringArray){
                                description = el;
                            }
                            System.out.println(description);
                            String workoutDescription = description.replace("}", "");
                            System.out.println(workoutDescription);

                            String jsonString = "{\'name\':\'" + workoutName + "\', \'description\':\"" + workoutDescription + "\"}";
                            System.out.println(jsonString.getClass().getName());
                            JSONObject workoutObject = new JSONObject(jsonString);

                            workoutModel workoutModel = new workoutModel();
                            workoutModel.setName(workoutObject.getString("name"));
                            workoutModel.setDescription(workoutObject.getString("description"));

                            workoutsListModel.add(workoutModel);
                            WorkoutAdapter workoutAdapter = new WorkoutAdapter(MainActivity.this, workoutsListModel);
                            workoutsListView.setAdapter(workoutAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return workoutsList;
        }
    }


    // called upon Save button click
    public void saveBtnClickHandler(View v) throws JSONException {

        DatabaseReference workoutsRef = usersRef.child(userID).child("workouts");
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        String workoutNameText = workoutName.getText().toString();
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        String workoutDescriptionText = workoutDescription.getText().toString();

        if (workoutDescriptionText.isEmpty()){
            showErrorMsg(workoutDescription, "Please enter the workout!");
        } else if (workoutNameText.isEmpty()){
            showErrorMsg(workoutName, "Please enter workout name!");
        } else {
            JSONObject workoutObject = new JSONObject();
            workoutObject.put("name", workoutNameText);
            workoutObject.put("description", workoutDescriptionText);
            Gson gson = new Gson();
            workoutPOJO workout = gson.fromJson(workoutObject.toString(), workoutPOJO.class);

            Date today = new Date();
            long timeInMillis = today.getTime();
            String millis = String.valueOf(timeInMillis);

            workoutsRef.child(millis).setValue(workout);
//        usersRef.child("workouts").child(workoutNameText).setValue(workoutDescriptionText);

            // notify user for the successful submission of the workout
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "Workout Notification");
            builder.setContentTitle("TheGymBuddy");
            builder.setContentText("Way to go! Another workout towards your goal body!");
            builder.setSmallIcon(R.drawable.ic_baseline_fitness_center_24);
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            managerCompat.notify( 1, builder.build() );

            Toast.makeText(MainActivity.this, "Workout Recorded", Toast.LENGTH_SHORT).show();
            workoutName.setText("");
            workoutDescription.setText("");
        }

    }

    private void showErrorMsg(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();
    }

    // called upon X button click
    public void revertChangesBtnClickHandler(View v){
        Toast.makeText(MainActivity.this, "Record Cleared", Toast.LENGTH_SHORT).show();
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        workoutName.setText("");
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        workoutDescription.setText("");
    }

    public void logOutBtnClickHandler(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(MainActivity.this, "You have been logged out successfuly. See you soon!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}

