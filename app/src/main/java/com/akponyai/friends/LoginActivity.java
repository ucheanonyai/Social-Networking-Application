package com.akponyai.friends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth; //initializing firebase

    EditText editTextUsername;
    EditText editTextPassword;
    Button loginButton;



    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        editTextUsername = (EditText) findViewById(R.id.usernameText);
        editTextPassword = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.loginButton);


        mAuth=FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        findViewById(R.id.textViewsignup).setOnClickListener(this);
        findViewById(R.id.loginButton).setOnClickListener(this);
    }

    private void userLogin(){
        String username=editTextUsername.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();


        if(username.isEmpty()){
            editTextUsername.setError("Email is required");
            editTextUsername.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            editTextUsername.setError("Please enter a valid email");
            editTextUsername.requestFocus();
            return;
        }

        if(password.length()<3){
            editTextPassword.setError("Minimum length of password should be 3");
            editTextPassword.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(LoginActivity.this,ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.textViewsignup:
                startActivity(new Intent(this,SignUpActivity.class));
                break;

            case R.id.loginButton:
                userLogin();
                break;
        }
    }

    public void addUser() {
        String name = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(password)) {
            String id = databaseUsers.push().getKey();

            Users user = new Users(name, password);

            databaseUsers.child(id).setValue(user);

            Toast.makeText(this, "User profile created", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Enter username or password", Toast.LENGTH_LONG).show();
        }

    }

}
