package com.example.moodly;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Victor on 2017-03-07.
 */

public class SocialUserSearch extends Fragment {

    protected User user;
    protected ArrayList<User> userList = new ArrayList<User>();
    protected UserAdapter adapter;
    protected ListView displayUserList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // adapt the moodlist onto our fragment using a custom MoodAdapter
        View rootView = inflater.inflate(R.layout.mood_history, container, false);
        adapter = new UserAdapter(getActivity(), R.layout.mood_list_item, userList);
        displayUserList = (ListView) rootView.findViewById(R.id.display_mood_list);
        displayUserList.setAdapter(adapter);

        // this activity should not have the add button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.hide();

        return rootView;
    }
}
