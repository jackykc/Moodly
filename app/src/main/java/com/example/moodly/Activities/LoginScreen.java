package com.example.moodly.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

public class LoginScreen extends AppCompatActivity {
    final ArrayList<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        setListeners();
    }

    protected void showInput(String dialogText){
        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View view = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
        builder.setView(view);
        builder.setTitle(dialogText);
        // Taken from http://stacktips.com/tutorials/android/android-input-dialog-example 3/11/2017
        final EditText userInput = (EditText)view.findViewById(R.id.usernameText);
        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = userInput.getText().toString();
                User person = new User(username);
                userList.add(person);
                Intent intent = new Intent(LoginScreen.this,ViewMoodList.class);
                Toast.makeText(LoginScreen.this,"Login Successful",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        builder.setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void setListeners(){
        Button loginButton = (Button) findViewById(R.id.Login);
        Button signupButton = (Button) findViewById(R.id.Register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput("Login");
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput("Sign Up");
            }
        });
    }
}
