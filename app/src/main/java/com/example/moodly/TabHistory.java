package com.example.moodly;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-05.
 */

/**
 * This class is a fragment to display moods from the mood history
 */
public class TabHistory extends TabBase {

    private int index = 0; // this should be used when selecting a mood from the list?


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
                final Mood mood = moodList.get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setMessage("Selecting mood to");
                adb.setCancelable(true);
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moodList.remove(mood);
                        adapter.notifyDataSetChanged();
                        displayMoodList.requestLayout();
                    }
                });
                adb.setNegativeButton("View/Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ViewMood.class);
                        MoodController.getInstance().setMood(new Mood());
                        startActivity(intent);
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
                startActivityForResult(intent, 0);

            }
        });

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

        // below is just for debugging
        Context debugContext = getContext();
        CharSequence debugText = "Adding mood";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(debugContext, debugText, duration);
        toast.show();

        refreshOffline();

    }

}



