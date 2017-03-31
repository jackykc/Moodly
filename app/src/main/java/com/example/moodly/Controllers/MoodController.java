package com.example.moodly.Controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.Mood;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
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
    public static ArrayList<Mood> moodHistoryList;
    private static ArrayList<Mood> moodFollowList;
    private static ArrayList<Mood> addSyncList;
    private static ArrayList<Mood> deleteSyncList;
    private static boolean addCompletetion;
    private static boolean deleteCompletetion;
    private static QueryBuilder queryBuilder;

    /**
     * Constructor for our mood controller, initializes members
     */
    private MoodController() {
        // replace when we do offline, load from file etc
        moodHistoryList = new ArrayList<Mood>();
        moodFollowList = new ArrayList<Mood>();
        tempMood = new Mood();
        addSyncList = new ArrayList<Mood>();
        deleteSyncList = new ArrayList<Mood>();
        queryBuilder = new QueryBuilder();

        addCompletetion = true;
        deleteCompletetion = true;
    }

    /**
     * Gets an instance of the mood controller
     * @return the controller
     */
    public static MoodController getInstance() {

        if(instance == null) {
            instance = new MoodController();
        }

        return instance;
    }


    /* ---------- Controller Functions ---------- */
    // Use these to interact with the views

    /**
     * Adds a mood both locally to the array list on the controller and on elastic search
     * @param position if position is -1, add to front of list, else update mood at position
     * @param m the moods to add/update
     */
    public void addMood(int position, Mood m) {
        if (position == -1) {
            // add to offline temporary list of moods
            moodHistoryList.add(0, m);
            addSyncList.add(m);
        } else {
            // maybe do a check for out of range here?
            moodHistoryList.set(position, m);
            if (m.getId() != null) {
                addSyncList.add(m);
            } else {
                // it is not on elastic search yet, therefore we update
                // locally
                Date date = m.getDate();

                for (int i = 0; i < addSyncList.size(); i++) {
                    if(addSyncList.get(i).getDate() == date) {
                        Mood temp = addSyncList.get(i);
                        addSyncList.set(i, temp);
                    }
                }
            }
        }
    }

    public boolean getAddCompletion() {
        return addCompletetion;
    }
    public boolean getDeleteCompletion() {
        return deleteCompletetion;
    }

    public void setCompletion(boolean completion) {
        addCompletetion = completion;
    }


    /**
     * Deletes a mood both locally from the array list on the controller and on elastic search
     * @param position position of the mood in the list to delete
     */
    public void deleteMood(int position) {

        Mood m = moodHistoryList.get(position);

        instance.moodHistoryList.remove(position);

        if (m.getId() != null) {
            //DeleteSyncTask deleteSyncTask = new DeleteSyncTask(m);
            deleteSyncList.add(m);
        } else {

            // it is not on elastic search yet, therefore we update
            // locally
            Date date = m.getDate();

            for (int i = 0; i < addSyncList.size(); i++) {
                if(addSyncList.get(i).getDate() == date) {
                    addSyncList.remove(i);
                    break;
                }
            }
        }


    }

    /**
     * Gets the moods by calling getMoodTask.execute() to get moods from elastic search
     * @param userList the list of users who we want the retrieved moods to belong to
     * @return a list of moods
     */
    public ArrayList<Mood> getMoodList (ArrayList<String> userList) {

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
    public void setMood(Mood mood) { tempMood = mood;}

    /* ---------- Elastic Search Requests ---------- */





    public void syncAddList() {

        if(addSyncList.size() > 0) {
            addCompletetion = false;
            AddMoodTask addMoodTask = new AddMoodTask();
            addMoodTask.execute(addSyncList);
        }
        else{
            addCompletetion = true;
        }

    }

    public void syncDeleteList() {

        if(deleteSyncList.size() > 0) {
            addCompletetion = false;
            DeleteMoodTask deleteMoodTask = new DeleteMoodTask();
            deleteMoodTask.execute(deleteSyncList);
        }

    }


    /**
     * Async task that adds a mood to elastic search
     */
    private static class AddMoodTask extends AsyncTask<ArrayList<Mood>, Void, Integer> {
        // return value is the number of moods that succeeded
        @Override
        protected Integer doInBackground(ArrayList<Mood>... moods){
            verifySettings();

            ArrayList<Mood> moodList = moods[0];
            int count = moodList.size();

            ArrayList<Index> bulkAction = new ArrayList<>();

            for(Mood mood : moodList) {
                bulkAction.add(new Index.Builder(mood).build());
            }


            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("cmput301w17t20").defaultType("mood")
                    .addAction(bulkAction)
                    .build();

            try {
                BulkResult result = client.execute(bulk);
                if (result.isSucceeded()) {

                } else {

                }
            }
            catch (Exception e) {
                Log.i("Error", "The application failed to build and send the mood");

                addCompletetion = true;
                return count;
            }

            // in case we add more elements to the list
            for (int j = 0; j < count; j++) {
                    addSyncList.remove(0);
            }

            addCompletetion = true;
            return count;
        }
    }




    /**
     * Async task that deletes a mood from elastic search
     */
    private static class DeleteMoodTask extends AsyncTask<ArrayList<Mood>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<Mood>... moods){
            verifySettings();

            ArrayList<Mood> moodList = moods[0];
            int count = moodList.size();
            ArrayList<Delete> bulkAction = new ArrayList<>();

            for(Mood mood : moodList) {
                bulkAction.add(new Delete.Builder(mood.getId()).build());
            }

            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("cmput301w17t20").defaultType("mood")
                    .addAction(bulkAction)
                    .build();

            try {
                BulkResult result = client.execute(bulk);
                if (result.isSucceeded()) {

                } else {
//                    deleteCompletetion = false;
//                    return false;
                }
            } catch (Exception e) {
                Log.i("Error", "The application failed to build and send the mood");
                deleteCompletetion = true;
                return false;
            }

            boolean commentsDeleted = false;

            String query = "{\n" +
                    "\t\"query\": {\n" +
                    "\t\t\"query_string\" : { \n" +
                    "\t\t\t\"fields\" : [\"moodId\"],\n" +
                    "\t\t\t\"query\" : \"";

            String queryMiddle = moodList.get(0).getId();

            for(int i = 1; i < moodList.size(); i++) {
                queryMiddle += " OR ";
                queryMiddle += moodList.get(i).getId();
            }

            String queryEnd = "\"\n" +
                    "\t\t}\n" +
                    "\t}\t\n" +
                    "}";

            query += queryMiddle;
            query += queryEnd;

            DeleteByQuery deleteComments = new DeleteByQuery.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("comment")
                    .build();

            try {
                JestResult result = client.execute(deleteComments);

                if (result.isSucceeded()) {
                    commentsDeleted = true;
                } else {
                    Log.i("Error", "Elasticsearch was not able to delete the comments");
                }
            } catch (Exception e) {
                Log.i("Error", "The application failed to build and delete the mood's comments");
                commentsDeleted = false;
                deleteCompletetion = true;
            }

            if(commentsDeleted) {
                // in case we add more elements to the list
                for (int j = 0; j < count; j++) {
                    deleteSyncList.remove(0);
                }
            }

            deleteCompletetion = true;

            return true;
        }
    }

    /**
     * Async task that gets an arraylist of moods from elastic search
     */
    private static class GetMoodTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Mood>> {
        @Override
        protected ArrayList<Mood> doInBackground(ArrayList<String>... search_parameters) {
            verifySettings();

            ArrayList<Mood> currentMoodList = new ArrayList<Mood>();


            ArrayList<String> usernames = search_parameters[0];

            if (usernames.size() == 0) { return new ArrayList<Mood>(); }

            queryBuilder.setUsers(usernames);
            String query = queryBuilder.getMoodQuery();

            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("mood")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<Mood, Void>> foundMoods = result.getHits(Mood.class);

                    for(int i = 0; i < foundMoods.size(); i++) {
                        Mood temp = foundMoods.get(i).source;
                        currentMoodList.add(temp);

                    }
                    // for your own list of moods
                    if ((usernames.size() == 1) &&(usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {
                        moodHistoryList = currentMoodList;
                    } else {
                        moodFollowList = currentMoodList;
                    }
                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return currentMoodList;
        }
    }

    /* ---------- Helpers ---------- */

    public ArrayList<Mood> getHistoryMoods () {
        return moodHistoryList;
    }

    public ArrayList<Mood> getFollowMoods () {
        return moodFollowList;
    }

    /* ---------- The following code is commented out for now as it is left for part 5 ---------- */

    // we can do binary search on this btw
    // and don't we have to sort it too?
    // sort what?


//    protected ArrayList<Mood> filterByDate(Date startDate, Date endDate) {
//        ArrayList<Mood> result = new ArrayList<>();
//        for (Mood m: moodList) {
//            if (m.getDate().after(startDate) && m.getDate().before(endDate)){
//                result.add(m);
//            }
//        }
//        return result;
//    }
//
//    protected ArrayList<Mood> filterByEmoState(Emotion e) {
//        ArrayList<Mood> result = new ArrayList<>();
//        for (Mood m: moodList) {
//            if (m.getEmotion().equals(e)){
//                result.add(m);
//            }
//        }
//        return  result;
//    }
//
//    protected ArrayList<Mood> filterByTextReason(String reason) {
//        ArrayList<Mood> result = new ArrayList<>();
//        for(Mood m:moodList) {
//            if (m.getReasonText().contains(reason)) {
//                result.add(m);
//            }
//        }
//        return result;
//    }



}