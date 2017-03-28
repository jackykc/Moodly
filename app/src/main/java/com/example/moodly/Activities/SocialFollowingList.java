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
 * Loads list of people that the current user follows from ElasticSearch
 *
 * @see SocialBase
 */

/**
 * SocialFollowingList class displays the current user's
 * following list. It sets the views of the user list and
 * updates when a change has been made.
 */
public class SocialFollowingList extends Fragment implements View.OnClickListener {

    protected UserController userController = UserController.getInstance();
    protected User currentUser;
    protected ArrayList<String> userList;

    private View rootView;
    private Button unfollowButton;
    protected ListView displayUserList;
    protected ArrayAdapter<String> adapter;

    /**
     * Synchronization of the user's following list
     * from ElasticSearch and sets the views to display
     * the list.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return rootView
     * @see #setViews(LayoutInflater, ViewGroup)
     * @see #refreshOnline()
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();
        userList = currentUser.getFollowing();
        setViews(inflater, container);

        return rootView;
    }
    /**
     * Gets the ListViews of the user list and displays
     * the current user's following list in a ListView format
     * @param inflater
     * @param container
     */
    protected void setViews(LayoutInflater inflater, ViewGroup container) {

        rootView = inflater.inflate(R.layout.social_list, container, false);

        displayUserList = (ListView) rootView.findViewById(R.id.display_social_list);
        displayUserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        unfollowButton = (Button) rootView.findViewById(R.id.unfollow_button);
        unfollowButton.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
        displayUserList.setAdapter(adapter);

    }

    /**
     * Synchronization of an user's following list using
     * ElasticSearch
     */
    protected void refreshOnline() {
        // add synchronization elements for part 5 here?
        currentUser = userController.getCurrentUser();
        //followers = userController.getFollowers();

    }

    public void onClick(View v) {
        SparseBooleanArray checked = displayUserList.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // get position in adapter
            int position = checked.keyAt(i);
            // add user if checked/TRUE
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }

        String[] outputStrArr = new String[selectedItems.size()];

        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }

        // DO SOMETHING WITH outputStrArr

    }
}

