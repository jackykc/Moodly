package com.example.moodly;

/**
 * Created by jkc1 on 2017-03-05.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;

import java.util.ArrayList;


/**
 * This class is a fragment to display moods from followed users
 */
public class TabBase extends Fragment {

    // we want to move the arraylist onto the controller
    protected Mood mood;
    protected ArrayList<Mood> moodList = new ArrayList<Mood>();
    protected MoodAdapter adapter;
    protected ListView displayMoodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // adapt the moodlist onto our fragment using a custom MoodAdapter
        View rootView = inflater.inflate(R.layout.mood_history, container, false);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        displayMoodList.setAdapter(adapter);

        // this activity should not have the add button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.hide();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {




    }

}
