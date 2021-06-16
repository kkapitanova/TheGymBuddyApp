package com.example.thegymbuddyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    EditText userNameInput;
    EditText userEmailInput;
    EditText userPasswordInput;
    EditText userPassConfirm;
    Button registerBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userNameInput = findViewById(R.id.userNameInput);
        userEmailInput = findViewById(R.id.userEmailInput);
        userPasswordInput = findViewById(R.id.userPassInput);
        userPassConfirm = findViewById(R.id.confirmPasswordInput);
        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(RegisterActivity.this);
        
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener((v) -> { 
            checkUserCredentials();
        });
        
        

        TextView btn = findViewById(R.id.AlreadyHaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    private void checkUserCredentials() {
        String userName = userNameInput.getText().toString();
        String userEmail = userEmailInput.getText().toString();
        String userPassword = userPasswordInput.getText().toString();
        String passConfirm = userPassConfirm.getText().toString();

        if (userName.isEmpty()){
            showErrorMsg(userNameInput, "Please enter your name!");
        } else if (userEmail.isEmpty() || !userEmail.contains("@")){
            showErrorMsg(userEmailInput, "Please enter a valid email!");
        } else if (userPassword.isEmpty() ||  userPassword.length() < 6){
            showErrorMsg(userPasswordInput, "Please enter a valid password!");
        } else if (passConfirm.isEmpty() || !passConfirm.equals(userPassword)){
            showErrorMsg(userPasswordInput, "Passwords do not match!");
        } else {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Hold on tight, we are creating your account.");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        FirebaseUser user = mAuth.getCurrentUser();
                        String userID = user.getUid();

                        mLoadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        mDatabase = database.getReference();
                        DatabaseReference usersRef = mDatabase.child("users");
                        usersRef.child(userID).child("name").setValue(userName);
                        Date today = new Date();
                        System.out.println(today.toString());
                        usersRef.child(userID).child("joined").setValue(today.toString());

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.putExtra("USER_ID", userID);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        mLoadingBar.dismiss();
                    }

                }
            });
        }

    }

    private void showErrorMsg(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();
    }


}