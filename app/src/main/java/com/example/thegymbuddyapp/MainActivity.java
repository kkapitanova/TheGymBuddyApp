package com.example.thegymbuddyapp;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.thegymbuddyapp.databinding.ActivityMainBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

private ActivityMainBinding binding;
    private DatabaseReference mDatabase;

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

        String userID = getIntent().getStringExtra("USER_ID");
        System.out.println(userID);

    }

    // called upon Save button click
    public void saveBtnClickHandler(View v){
        TextView text = findViewById(R.id.testText);
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        CharSequence workoutNameText = workoutName.getText();
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        CharSequence workoutDescriptionText = workoutDescription.getText();
        System.out.println(workoutNameText);
        System.out.println(workoutDescriptionText);
        text.setText(workoutNameText);

    }

    // called upon X button click
    public void revertChangesBtnClickHandler(View v){
        TextView text = findViewById(R.id.testText);
        TextInputEditText workoutName = findViewById(R.id.workoutNameInput);
        workoutName.setText("");
        EditText workoutDescription = findViewById(R.id.workoutDescription);
        workoutDescription.setText("");
        text.setText("input cleared");
    }

}