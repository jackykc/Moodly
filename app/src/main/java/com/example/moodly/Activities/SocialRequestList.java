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

public class SocialRequestList extends Fragment implements View.OnClickListener {

    protected UserController userController = UserController.getInstance();
    protected User currentUser;
    protected ArrayList<String> userList;

    private View rootView;
    private Button acceptRequestButton;
    protected ListView displayUserList;
    protected ArrayAdapter<String> adapter;

    // PLACEHOLDER
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        refreshOnline();
        userList = currentUser.getRequests();
        setViews(inflater, container);

        return rootView;
    }

    protected void setViews(LayoutInflater inflater, ViewGroup container) {

        rootView = inflater.inflate(R.layout.social_request_list, container, false);

        displayUserList = (ListView) rootView.findViewById(R.id.display_request_list);
        displayUserList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        acceptRequestButton = (Button) rootView.findViewById(R.id.accept_request_button);
        acceptRequestButton.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, userList);
        displayUserList.setAdapter(adapter);

    }

    // Sync up offline and online
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
