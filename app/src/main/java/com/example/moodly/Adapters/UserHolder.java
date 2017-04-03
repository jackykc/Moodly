package com.example.moodly.Adapters;

import android.widget.CheckBox;

import com.example.moodly.Models.Mood;
import com.example.moodly.Models.User;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

/**
 * UserHolder holds a user and its mood list.
 */
public class UserHolder {
    User user;
    CheckBox checkBox;
    ArrayList<Mood> moodList = new ArrayList<>();
}
