package com.akponyai.friends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    EditText usernameText;
    EditText passwordText;
    Button loginButton;



    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        loginButton = (Button) findViewById(R.id.signupButton);




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        findViewById(R.id.textViewsignup).setOnClickListener(this);
    }

    public void onClick(View view){
        switch(view.getId()){
            case R.id.textViewsignup:
                startActivity(new Intent(this,SignUpActivity.class));
                break;
        }
    }

    public void addUser() {
        String name = usernameText.getText().toString();
        String password = passwordText.getText().toString();

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
