package com.example.moodly.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.User;
import com.example.moodly.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
    static String FILE_NAME = "UserName";
    Context context;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    static List<User> users = new ArrayList<>();

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
        //sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String logout = getIntent().getStringExtra("toClear");
        if (logout != null) {
            users.clear();
            saveInFile();
        }
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

        loadFromFile();
        if (users.size() != 0 && networkAvailable()) {
            conn.setCurrentUser(users.get(0).getName());
            hello(users.get(0).getName());
            startActivity(intent);
        }
        else if (users.size() != 0 && !networkAvailable()) {
            conn.setCurrentUserOffline(users.get(0));
            hello(conn.getCurrentUser().getName());
            startActivity(intent);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkAvailable()) {
                    conn.setCurrentUser(userName.getText().toString());
                    if (conn.getCurrentUser() != null) {
                        hello(userName.getText().toString());
//                    editor.putString("UserName", userName.getText().toString());
//                    editor.commit();
                        users.add(conn.getCurrentUser());
                        saveInFile();
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginScreen.this, "This username does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginScreen.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });

//         No sign up functionality yet
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkAvailable()) {
                    conn.setCurrentUser(userName.getText().toString());
                    if (conn.getCurrentUser() == null) {
                        conn.createUser(userName.getText().toString());
                        hello(userName.getText().toString());
                        users.add(conn.getCurrentUser());
                        saveInFile();
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginScreen.this, "This username already exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginScreen.this, "Network unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            users = gson.fromJson(in, new TypeToken<ArrayList<User>>(){}.getType());
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            users = new ArrayList<User>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    private void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            Gson gson = new Gson();
            gson.toJson(users, out);
            out.flush();

            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

}