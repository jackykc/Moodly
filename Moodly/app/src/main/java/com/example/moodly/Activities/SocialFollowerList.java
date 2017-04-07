package com.example.moodly.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.moodly.Controllers.UserController;
import com.example.moodly.Models.User;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by Victor on 2017-03-08.
 * Fragment to be displayed on SocialBase
 * Loads list of Followers of current User from ElasticSearch
 *
 * @see SocialBase
 */

public class SocialFollowerList extends Fragment {

    protected UserController userController = UserController.getInstance();
    protected User currentUser;
    protected ArrayList<String> userList;

    private View rootView;
    protected ListView displayUserList;
    protected ArrayAdapter<String> adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();
        userList = currentUser.getFollowers();
        setViews(inflater, container);

        return rootView;
    }

    protected void setViews(LayoutInflater inflater, ViewGroup container) {

        rootView = inflater.inflate(R.layout.social_follower_list, container, false);

        displayUserList = (ListView) rootView.findViewById(R.id.display_follower_list);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
        displayUserList.setAdapter(adapter);

    }

    // Sync up offline and online
    protected void refreshOnline() {
        // add synchronization elements for part 5 here?
        currentUser = userController.getCurrentUser();
        //followers = userController.getFollowers();

    }



}

