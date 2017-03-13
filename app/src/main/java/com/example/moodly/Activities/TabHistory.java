package com.example.moodly.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.moodly.Adapters.MoodAdapter;
import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-05.
 */

/**
 * This class is a fragment to display moods from the mood history
 */
public class TabHistory extends TabBase {

    private MoodAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentUser = userController.getCurrentUser();
        userList = new ArrayList<String>();
        userList.add(currentUser.getName());
        // tries to get moods from elastic search server
        refreshOnline(userList);
        setViews(inflater, container);
        setListeners();

        return rootView;
    }

    /**
     * Sets listeners for the activity
     */
    @Override
    protected void setListeners() {

        displayMoodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // why do we need this?
                final Mood mood = moodList.get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setMessage("Selecting mood to");
                adb.setCancelable(true);
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MoodController.getInstance().deleteMood(position);
                        refreshOffline();

                    }
                });
                adb.setNegativeButton("View/Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ViewMood.class);
                        // 2 means edit
                        intent.putExtra("MOOD_POSITION", position);
                        MoodController.getInstance().setMood(mood);
                        startActivityForResult(intent, 0);
                    }
                });
                adb.show();
                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MoodController.getInstance().setMood(new Mood());
                Intent intent = new Intent(getActivity(), ViewMood.class);
                // 1 means add
                intent.putExtra("MOOD_POSITION", -1);
                startActivityForResult(intent, 0);

            }
        });

    }

    /**
     * On the result of adding or editing moods, refreshes the mood list
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        refreshOffline();
    }

    /**
     * Sets the views for the activity
     * @param inflater the layout inflater
     * @param container the view group
     */
    @Override
    protected void setViews(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.mood_history, container, false);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);

    }

    /**
     * Gets the latest mood list from the controller and refreshes adapters
     */
    @Override
    protected void refreshOffline() {
        moodList = MoodController.getInstance().getHistoryMoods();

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}



