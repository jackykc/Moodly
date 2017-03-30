package com.example.moodly.Controllers;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.Mood;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import static junit.framework.Assert.assertEquals;

/**
 * Created by MinhNguyen on 06/03/2017.
 */

/**
 * Mood controller that allows access to moods on elastic search
 * @author Jacky Chung
 */
public class MoodController extends ElasticSearchController {

    private static MoodController instance = null;
    private Mood tempMood;
    private static ArrayList<Mood> moodList;
    public static ArrayList<Mood> moodHistoryList;
    private static ArrayList<Mood> moodFollowList;
    private ArrayList<Mood> filteredList;

    private static QueryBuilder queryBuilder;

    /**
     * Constructor for our mood controller, initializes members
     */
    private MoodController() {
        // replace when we do offline, load from file etc
        moodList = new ArrayList<Mood>();
        moodHistoryList = new ArrayList<Mood>();
        moodFollowList = new ArrayList<Mood>();
        filteredList = new ArrayList<Mood>();
        tempMood = new Mood();

        queryBuilder = new QueryBuilder();
    }

    /**
     * Gets an instance of the mood controller
     *
     * @return the controller
     */
    public static MoodController getInstance() {

        if (instance == null) {
            instance = new MoodController();
        }

        return instance;
    }


    /* ---------- Controller Functions ---------- */
    // Use these to interact with the views

    /**
     * Adds a mood both locally to the array list on the controller and on elastic search
     *
     * @param position if position is -1, add to front of list, else update mood at position
     * @param m        the moods to add/update
     */
    public void addMood(int position, Mood m) {

        // add to local list
        if (queryBuilder.isValid(m)) {

            if (position == -1) {
                // add to offline temporary list of moods
                moodHistoryList.add(0, m);
            } else {
                moodHistoryList.set(position, m);
            }

        }
        // add to elastic search
        MoodController.AddMoodTask addMoodTask = new MoodController.AddMoodTask();
        addMoodTask.execute(m);

    }


    /**
     * Deletes a mood both locally from the array list on the controller and on elastic search
     *
     * @param position position of the mood in the list to delete
     */
    public void deleteMood(int position) {

        Mood m = moodHistoryList.get(position);

        instance.moodHistoryList.remove(position);

        MoodController.DeleteCommentsTask deleteCommentsTask = new MoodController.DeleteCommentsTask();
        deleteCommentsTask.execute(m);

        MoodController.DeleteMoodTask deleteMoodTask = new MoodController.DeleteMoodTask();
        deleteMoodTask.execute(m);


    }

    /**
     * Gets the moods by calling getMoodTask.execute() to get moods from elastic search
     *
     * @param userList the list of users who we want the retrieved moods to belong to
     * @return a list of moods
     */
    public ArrayList<Mood> getMoodList(ArrayList<String> userList) {

        MoodController.GetMoodTask getMoodTask = new MoodController.GetMoodTask();
        getMoodTask.execute(userList);
        // do I need to construct the array list or can i just declare it?
        ArrayList<Mood> tempMoodList = new ArrayList<Mood>();
        try {
            tempMoodList = getMoodTask.get();
        } catch (Exception e) {
            Log.i("Error", "Failed to get mood out of async object");
        }
        return tempMoodList;
    }

    // sets the emotion to filter for
    public void setEmotion(int emotion) {
        queryBuilder.setEmotion(emotion);
    }

    // set to true if we want moods from last seven days
    public void setRecent(boolean recent) {
        queryBuilder.setRecent(recent);
    }

    // set the single word to search for in reason text
    public void setRecent(String reason) {
        queryBuilder.setReason(reason);
    }


    public Mood getMood() {
        return tempMood;
    }

    public void setMood(Mood mood) {
        tempMood = mood;
    }

    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async task that adds a mood to elastic search
     */
    private static class AddMoodTask extends AsyncTask<Mood, Void, Void> {

        int completion = 0;

        @Override
        protected Void doInBackground(Mood... moods) {
            verifySettings();

            for (Mood mood : moods) {

                Index index = new Index.Builder(mood).index("cmput301w17t20").type("mood").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        if (mood.getId() == null) {
                            mood.setId(result.getId());
                            // assumption method addMood always runs before this
                            // if the id is not set, set it
                            if (moodHistoryList.get(0).getId() == null) {
                                moodHistoryList.get(0).setId(result.getId());
                            }

                        }


                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the mood");
                    }
                    // where is the client?
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the mood");
                }

            }

            return null;
        }
    }

    private static class DeleteCommentsTask extends AsyncTask<Mood, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Mood... moods) {
            verifySettings();

            for (Mood mood : moods) {

                // Did I include id twice?
                // if it works don't change it?
                //Delete delete = new Delete.Builder(mood.getId()).index("cmput301w17t20").type("comment").id(mood.getId()).build();
                String query = "{\n" +
                        "\t\"query\": {\n" +
                        "\t\t\"match\": {\n" +
                        "\t\t\t\"moodId\": \" " + mood.getId() + "\"\n" +
                        "\t\t}\n" +
                        "\t}\n" +
                        "}";

                DeleteByQuery deleteComments = new DeleteByQuery.Builder(query)
                        .addIndex("cmput301w17t20")
                        .addType("comment")
                        .build();

                //http://stackoverflow.com/questions/34760557/elasticsearch-delete-by-query-using-jest

                try {
                    JestResult result = client.execute(deleteComments);
                    if (result.isSucceeded()) {
                        return true;
                    } else {
                        Log.i("Error", "Elasticsearch was not able to delete the comments");
                    }
                    // where is the client?
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and delete the mood's comments");
                }

            }

            return false;
        }
    }

    /**
     * Async task that deletes a mood from elastic search
     */
    private static class DeleteMoodTask extends AsyncTask<Mood, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Mood... moods) {
            verifySettings();

            for (Mood mood : moods) {

                // Did I include id twice?
                // if it works don't change it?
                Delete delete = new Delete.Builder(mood.getId()).index("cmput301w17t20").type("mood").id(mood.getId()).build();

                try {
                    DocumentResult result = client.execute(delete);
                    if (result.isSucceeded()) {
                        return true;
                    } else {
                        Log.i("Error", "Elasticsearch was not able to delete the mood");
                    }
                    // where is the client?
                } catch (Exception e) {
                    Log.i("Error", "The application failed to build and delete the mood");
                }

            }

            return false;
        }
    }

    /**
     * Async task that gets an arraylist of moods from elastic search
     */
    private static class GetMoodTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(ArrayList<String>... search_parameters) {
            verifySettings();

            ArrayList<String> usernames = search_parameters[0];

            if (usernames.size() == 0) {
                return new ArrayList<Mood>();
            }

            String query = "";
            if ((usernames.size() == 1) && (usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {

                queryBuilder.resultOffset(moodHistoryList.size());
                queryBuilder.setUsers(usernames);
                query = queryBuilder.getMoodQuery();

            } else {
                queryBuilder.resultOffset(moodFollowList.size());
                queryBuilder.setUsers(usernames);
                query = queryBuilder.getMoodQuery();

            }
            
            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("mood")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<Mood, Void>> foundMoods = result.getHits(Mood.class);

                    // for your own list of moods
                    if ((usernames.size() == 1) && (usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {
                        for (int i = 0; i < foundMoods.size(); i++) {
                            Mood temp = foundMoods.get(i).source;
                            moodHistoryList.add(temp);

                        }
                    } else {
                        for (int i = 0; i < foundMoods.size(); i++) {
                            Mood temp = foundMoods.get(i).source;
                            moodFollowList.add(temp);

                        }
                    }
                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            if ((usernames.size() == 1) && (usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {
                return moodHistoryList;
            } else {
                return moodFollowList;
            }
        }
    }

    /* ---------- Helpers ---------- */

    public ArrayList<Mood> getHistoryMoods() {
        return moodHistoryList;
    }

    public ArrayList<Mood> getFollowMoods() {
        return moodFollowList;
    }

}