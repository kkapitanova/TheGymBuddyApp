package com.example.thegymbuddyapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thegymbuddyapp.ui.profile.ProfileFragment;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

private ActivityMainBinding binding;
    private DatabaseReference mDatabase;
    String userName;
    DatabaseReference usersRef;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityMainBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

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
                userName = snapshot.child(userID).child("name").getValue().toString();
                System.out.println("in ondatachange function");
                System.out.println(userName);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    // called upon Save button click
    public void saveBtnClickHandler(View v) throws JSONException {

        DatabaseReference workoutsRef = usersRef.child(userID).child("workouts");
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        String workoutNameText = workoutName.getText().toString();
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        String workoutDescriptionText = workoutDescription.getText().toString();

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

    // called upon X button click
    public void revertChangesBtnClickHandler(View v){
        Toast.makeText(MainActivity.this, "Record Cleared", Toast.LENGTH_SHORT).show();
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        workoutName.setText("");
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        workoutDescription.setText("");
    }

}