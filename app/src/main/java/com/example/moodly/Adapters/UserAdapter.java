package com.example.moodly.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.moodly.Adapters.MoodHolder;
import com.example.moodly.Models.User;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class UserAdapter extends ArrayAdapter<User> {

    private ArrayList<User> following = new ArrayList<>();
    private ArrayList<User> follower = new ArrayList<>();

    private String username;

    private ArrayList<User> items;
    private int layoutResourceId;
    private MoodHolder holder;
    private Context context;

    /**
     * Constructor for our UserAdapter
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public UserAdapter(Context context, int layoutResourceId, ArrayList<User> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

}
