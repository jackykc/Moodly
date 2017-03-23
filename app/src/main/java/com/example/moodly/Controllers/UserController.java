package com.example.moodly.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.User;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by jkc1 on 2017-03-11.
 */


/**
 * User controller that allows access to users on elastic search
 * @author Jacky Chung
 */
public class UserController extends ElasticSearchController {

    private static UserController instance = null;
    private static User currentUser;
    private String currentUsername = null; // guessing we can remove this?
    private static ArrayList<User> following;
    private static ArrayList<User> followers;

    private UserController() {
        following = new ArrayList<User>();
        followers = new ArrayList<User>();
    }


    /**
     * Gets an instance of the user controller
     * @return the controller
     */
    public static UserController getInstance() {
        if(instance == null) {
            instance = new UserController();
        }

        return instance;
    }


    public ArrayList<User> getFollowers () {
        return followers;
    }
    /* ---------- Controller Functions ---------- */

    /**
     * Used to create a new use, not implemented yet
     */
    public void createUser(String name) {
        User u = new User(name);
        AddUserTask addUserTask = new AddUserTask();
        addUserTask.execute(u);
        currentUser = u;
    }

    public void setCurrentUser(String name){
        UserController.GetUserTask getUserTask = new UserController.GetUserTask();
        getUserTask.execute(name);

        try {
            currentUser = getUserTask.get();
        } catch (Exception e) {
            Log.i("Error", "Cannot get current user out of async object");
        }
    }

    public ArrayList<User> searchUsers(String searchText){
        UserController.SearchUsersTask searchUsersTask = new UserController.SearchUsersTask();
        searchUsersTask.execute(searchText);

        ArrayList<User> userList = new ArrayList<User>();
        try {
            userList = searchUsersTask.get();
        } catch (Exception e) {
            Log.i("Error", "Cannot get current user out of async object");
        }

        return userList;

    }

    /**
     * Gets the user that logged in the app
     * @return current user
     */
    public User getCurrentUser() {

        if (currentUser == null) {
            UserController.GetUserTask getUserTask = new UserController.GetUserTask();
            getUserTask.execute();

            try {
                currentUser = getUserTask.get();

            } catch (Exception e) {
                Log.i("Error", "Cannot get current user out of async object");
            }
        }

        return currentUser;
    }


    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async tasks that gets the list of users from elastic search
     */
    private static class GetUsersTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<User> userList = new ArrayList<User>();

            String query = "";
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

                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return userList;
        }
    }

    private static class SearchUsersTask extends AsyncTask<String, Void, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<User> userList = new ArrayList<User>();

            String query =
                    "{ \n\"query\" : {\n" +
                            "    \"match_phrase_prefix\" : { \"name\" : \"" + search_parameters[0] +
                            "\"     }\n " +
                            "    }\n" +
                            " } ";

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

                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return userList;
        }
    }


    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async task that gets the current user's user object from elastic search
     */
    private static class GetUserTask extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... search_parameters) {
            verifySettings();

            if (search_parameters.length == 0) {return null;}

            String query =
            "{ \n\"query\" : {\n" +
                    "    \"match\" : { \"name\" : \"" + search_parameters[0] +
                    "\"     }\n " +
                    "    }\n" +
                    " } ";

            // TODO Build the query
            System.out.println(query);
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("user")
                    .build();


            User temp = null;
            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<User, Void>> foundUsers = result.getHits(User.class);
                    temp = foundUsers.get(0).source;

                    currentUser = temp;

                } else {
                    Log.i("Error", "Search query failed to find any users that matched");
                }
            }
            catch (Exception e) {

            }

            return temp;
        }
    }

    private static class AddUserTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users){
            verifySettings();

            for(User user : users) {

                Index index = new Index.Builder(user).index("cmput301w17t20").type("user").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        if (user.getId() == null) {
                            user.setId(result.getId());
                            // dont think we have a userlist ?!?
//                            if(moodHistoryList.get(0).getId() == null) {
//                                moodHistoryList.get(0).setId(result.getId());
//                            }

                        }


                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the mood");
                    }
                    // where is the client?
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the mood");
                }

            }

            return null;
        }
    }

}
