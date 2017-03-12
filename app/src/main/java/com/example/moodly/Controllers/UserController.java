package com.example.moodly.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.User;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by jkc1 on 2017-03-11.
 */

public class UserController extends ElasticSearchController {

    private static UserController instance = null;
    private User currentUser;
    private static ArrayList<User> followingList;
    private static ArrayList<User> followerList;

    private UserController() {
        currentUser = new User();
        followingList = new ArrayList<User>();
        followerList = new ArrayList<User>();
    }

    public static UserController getInstance() {

        if(instance == null) {
            instance = new UserController();
        }

        return instance;
    }

    /* ---------- Controller Functions ---------- */
    public void createUser() {

    }

    public ArrayList<User> getFollowingList() {

        ArrayList<User> tempFollowingList = new ArrayList<User>();

        return tempFollowingList;
    }


    /* ---------- Elastic Search Requests ---------- */
    // untested, returns arraylist of users from elastic search
    private static class GetMoodTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<User> userList = new ArrayList<User>();
            // hahaha how do i even make a query string?????
            String query = "{\"sort\": { \"date\": { \"order\": \"desc\"}}}";

            query = "";
            // TODO Build the query
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("user")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<User, Void>> foundUsers = result.getHits(User.class);

                    for(int i = 0; i < foundUsers.size(); i++) {
                        User temp = foundUsers.get(i).source;

                        userList.add(temp);
                    }
                    //moodList = currentMoodList;

                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            // ??? not needed?
            return userList;
        }
    }

}
