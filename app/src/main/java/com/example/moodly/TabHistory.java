package com.example.moodly;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-05.
 */

public class TabHistory extends TabBase {

    private int index = 0; // this should be used when selecting a mood from the list?


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.mood_history, container, false);

        adapter = new MoodAdapter(getActivity(), R.layout.mood_list_item, moodList);
        displayMoodList = (ListView) rootView.findViewById(R.id.display_mood_list);
        displayMoodList.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mood = new Mood();
                Intent intent = new Intent(getActivity(), ViewMood.class);
                intent.putExtra("PLACEHOLDER_MOOD", mood);
                startActivityForResult(intent, 0);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mood = data.getParcelableExtra("VIEWMOOD_MOOD");
        moodList.add(mood);
        adapter.notifyDataSetChanged();
    }


}

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_mood_list);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mood = new Mood();
//                Intent intent = new Intent(ViewMoodList.this, ViewMood.class);
//                intent.putExtra("PLACEHOLDER_MOOD", mood);
//                startActivityForResult(intent, 0);
//
//                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                //        .setAction("Action", null).show();
//
//            }
//        });
//
//    }



