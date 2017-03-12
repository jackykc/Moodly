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

/**
 * Created by jkc1 on 2017-03-05.
 */

/**
 * This class is a fragment to display moods from the mood history
 */
public class TabHistory extends TabBase {

    private int index = 0; // this should be used when selecting a mood from the list?
    private MoodAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // tries to get moods from elastic search server
        refreshOnline();
        setViews(inflater, container);
        setListeners();

        return rootView;
    }



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

        /*
        Button refresh = (Button) rootView.findViewById(R.id.refreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMoodList.deferNotifyDataSetChanged();
            }
        });
        */

    }

    // do we need this? maybe checking connection?
    @Override
    public void onStart() {
        super.onStart();

    }

    // is this where we should refresh? man i don't know right now
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//         below is just for debugging
//        Context debugContext = getContext();
//        CharSequence debugText = "Adding mood";
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(debugContext, debugText, duration);
//        toast.show();

        //refreshOnline();
        refreshOffline();

    }

    @Override
    protected void setViews(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.mood_history, container, false);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);

    }

    @Override

    protected void refreshOffline() {
        moodList = MoodController.getInstance().getFiltered();

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);
        // needed ?
        adapter.notifyDataSetChanged();
    }

}



