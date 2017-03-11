package com.example.moodly;

/**
 * Created by jkc1 on 2017-03-05.
 */


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
    protected MoodAdapter adapter;
    protected ListView displayMoodList;
    protected ArrayList<Mood> moodList = new ArrayList<Mood>();
    protected View rootView;

    protected MoodController moodController = MoodController.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();

        setViews(inflater, container);
        hideViews();

        setListeners();

        return rootView;
    }

    protected void setViews(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.mood_history, container, false);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);

    }

    // specific to hiding buttons etc from follower list
    protected void hideViews() {
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.hide();

    }

    protected void setListeners() {

    }

    /* ---------- Refreshing Moods ---------- */
    // by part 5 of the project these two will be reduced to a single method

    protected void refreshOnline() {
        // add synchronization elements for part 5 here?
        moodList = MoodController.getInstance().getMoodList();

    }

    protected void refreshOffline() {
        moodList = MoodController.getInstance().getFiltered();

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);
        // needed ?
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
