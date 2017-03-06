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


public class TabBase extends Fragment {

    protected Mood mood;
    protected ArrayList<Mood> moodList = new ArrayList<Mood>();
    protected MoodAdapter adapter;
    protected ListView displayMoodList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mood_history, container, false);
        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        displayMoodList.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {




    }

}
