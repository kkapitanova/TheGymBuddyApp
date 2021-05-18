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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    EditText userEmailInput;
    EditText userPasswordInput;
    Button loginBtn;
    private FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmailInput = findViewById(R.id.emailInput);
        userPasswordInput = findViewById(R.id.passwordInput);
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener((v) -> {
            checkUserCredentials();
        });
        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(LoginActivity.this);



        TextView btn = findViewById(R.id.NoAccount);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void checkUserCredentials() {
        String userEmail = userEmailInput.getText().toString();
        String userPassword = userPasswordInput.getText().toString();

        if (userEmail.isEmpty() || !userEmail.contains("@")){
            showErrorMsg(userEmailInput, "Please enter a valid email!");
        } else if (userPassword.isEmpty() || userPassword.length() < 6){
            showErrorMsg(userPasswordInput,"Password must be at least 6 characters");
        } else {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Welcome Back! Give us a second to verify this is you.");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        System.out.println(String.valueOf(task.getResult().getUser().getUid()));

                        mLoadingBar.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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