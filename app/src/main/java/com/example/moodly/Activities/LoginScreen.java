package com.example.moodly.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.R;

/**
 * This class is the login screen of the app.
 * Users can either login or sign up for an account on the app.
 * Users are stored in a master list of users that will be used
 * to add and confirm users as they use the app.
 */
public class LoginScreen extends AppCompatActivity {
    //final ArrayList<User> userList = new ArrayList<>();
    EditText userName;
    Button loginButton;
    Button signUpButton;
    Intent intent;
    UserController conn = UserController.getInstance();
    static String FILE_NAME = "SharedPref";
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /**
     * Creates the MoodBase intent and calls upon setListeners()
     * @param savedInstanceState
     * @see #setListeners()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        userName = (EditText) findViewById(R.id.userName);
        intent = new Intent(getApplicationContext(), MoodBase.class);
        context = getApplicationContext();
        sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        setListeners();
    }

    /**
     * Provides a short toast to current user logging in
     */
    private void hello(String name) {
        String greetings = "Hello, " + name;
        Toast.makeText(LoginScreen.this, greetings, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets listeners for login and sign up buttons
     *
     */
    protected void setListeners(){
        loginButton = (Button) findViewById(R.id.Login);
        signUpButton = (Button) findViewById(R.id.Register);

        editor = sharedPref.edit();
        String user = sharedPref.getString("UserName", null);
        System.out.println(user);
        if (user != null) {
            conn.setCurrentUser(user);
            hello(user);
            startActivity(intent);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setCurrentUser(userName.getText().toString());
                if (conn.getCurrentUser() != null) {
                    hello(userName.getText().toString());
                    editor.putString("UserName", userName.getText().toString());
                    editor.commit();
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginScreen.this, "This username does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

//         No sign up functionality yet
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setCurrentUser(userName.getText().toString());
                if (conn.getCurrentUser() == null) {
                    conn.createUser(userName.getText().toString());
                    hello(userName.getText().toString());
                    editor.putString("UserName", userName.getText().toString());
                    editor.commit();
                    startActivity(intent);
                }
                else {
                    Toast.makeText(LoginScreen.this, "This username already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}