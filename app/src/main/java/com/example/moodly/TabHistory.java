package com.example.moodly;

import android.content.Context;
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

        View rootView = inflater.inflate(R.layout.mood_history, container, false);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);

        // uses controller to get moods from elastic search
        //MoodController.GetMoodTask getMoodTask = new MoodController.GetMoodTask();
        //moodList = getMoodTask.execute("Jacky");

        // adapt the moodlist onto our fragment using a custom MoodAdapter
        //adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, MoodController.getInstance().getFiltered());
        //displayMoodList.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MoodController.getInstance().setMood(new Mood());
                Intent intent = new Intent(getActivity(), ViewMood.class);
                startActivityForResult(intent, 0);

            }
        });

        return rootView;
    }

    // problem with this on onCreateView
    // actually its either this or onCreateView that gets run right after
    // onActivityResult, it nullifies the add mood being shown as this onStart
    // resets the adapter with only the first 10 moods
    @Override
    public void onStart() {
        super.onStart();

        MoodController.GetMoodTask getMoodTask = new MoodController.GetMoodTask();
        getMoodTask.execute("");
        try {
            moodList = getMoodTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get mood out of async object");
        }

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // below is just for debugging
        Context debugContext = getContext();
        CharSequence debugText = "Adding mood";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(debugContext, debugText, duration);
        toast.show();

        // either use the get mood task
//        MoodController.GetMoodTask getMoodTask = new MoodController.GetMoodTask();
//        getMoodTask.execute("");
//        try {
//            moodList = getMoodTask.get();
//            adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, MoodController.getInstance().getFiltered());
//            displayMoodList.setAdapter(adapter);
//        } catch (Exception e) {
//            Log.i("Error", "Failed to get mood out of async object");
//        }
//        // or

        moodList.add(MoodController.getInstance().getMood());

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList.setAdapter(adapter);
        // needed ?
        adapter.notifyDataSetChanged();
    }

}




