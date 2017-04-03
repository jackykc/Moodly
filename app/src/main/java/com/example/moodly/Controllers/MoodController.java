package com.example.moodly.Controllers;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.Mood;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;


/**
 * Mood controller that allows access to moods on elastic search
 *
 * @author Jacky Chung
 */
public class MoodController extends ElasticSearchController {

    private static MoodController instance = null;
    private Mood tempMood;
    private static ArrayList<Mood> moodHistoryList;
    private static ArrayList<Mood> moodFollowList;
    private static ArrayList<Mood> moodNearbyList;

    private static ArrayList<Mood> addSyncList;
    private static ArrayList<Mood> deleteSyncList;
    private static boolean addCompletion;
    private static boolean deleteCompletion;

    private static boolean refresh;

    private static QueryBuilder queryBuilder;
    private static QueryBuilder followQueryBuilder;

    private static double latitude;
    private static double longitude;

    /**
     * Constructor for our mood controller, initializes members
     */
    private MoodController() {
        // replace when we do offline, load from file etc
        moodHistoryList = new ArrayList<>();
        moodFollowList = new ArrayList<>();
        tempMood = new Mood();

        addSyncList = new ArrayList<Mood>();
        deleteSyncList = new ArrayList<Mood>();

        queryBuilder = new QueryBuilder();
        followQueryBuilder = new QueryBuilder();

        addCompletion = true;
        deleteCompletion = true;
        refresh = true;

        latitude = 0;
        longitude = 0;
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

    /**
     * Gets locations from a list of moods
     *
     * @param listType integer representing which mood list to use
     * @return an ArrayList of LatLng associated with the moods
     */
    public ArrayList<LatLng> getLocations(int listType) {
        ArrayList<LatLng> returnList = new ArrayList<LatLng>();

        if(listType == 0) {
            for (int i = 0; i < moodHistoryList.size(); i++) {
                returnList.add(moodHistoryList.get(i).getLocation());
            }
        } else if (listType == 1){
            for (int i = 0; i < moodFollowList.size(); i++) {
                returnList.add(moodFollowList.get(i).getLocation());
            }
        } else {
            for (int i = 0; i < moodNearbyList.size(); i++) {
                returnList.add(moodNearbyList.get(i).getLocation());
            }
        }

        return returnList;
    }

    /**
     * Gets emotions from a list of moods
     *
     * @param listType integer representing which mood list to use
     * @return an array list of emotions associated with the moods
     */
    public ArrayList<Integer> getEmotions(int listType) {

        ArrayList<Integer> returnList = new ArrayList<Integer>();

        if(listType == 0) {
            for (int i = 0; i < moodHistoryList.size(); i++) {
                returnList.add(moodHistoryList.get(i).getEmotion());
            }
        } else if (listType == 1){
            for (int i = 0; i < moodFollowList.size(); i++) {
                returnList.add(moodFollowList.get(i).getEmotion());
            }
        } else {
            for (int i = 0; i < moodNearbyList.size(); i++) {
                returnList.add(moodNearbyList.get(i).getEmotion());
            }
        }

        return returnList;
    }

    /* ---------- Controller Functions ---------- */

    /**
     * Adds a mood both locally to the array list on the controller and on elastic search
     *
     * @param position if position is -1, add to front of list, else update mood at position
     * @param m        the moods to add/update
     */
    public void addMood(int position, Mood m) {

        if (position == -1) {
            // add to offline temporary list of moods
            if(queryBuilder.withinFilter(m)) {
                moodHistoryList.add(0, m);
            }
            addSyncList.add(m);
        } else {
            // maybe do a check for out of range here?
            moodHistoryList.set(position, m);
            if (m.getId() != null) {
                addSyncList.add(m);
            } else {
                // it is not on elastic search yet, therefore we update locally
                Date date = m.getDate();

                for (int i = 0; i < addSyncList.size(); i++) {
                    if (addSyncList.get(i).getDate() == date) {
                        Mood temp = addSyncList.get(i);
                        addSyncList.set(i, temp);
                    }
                }
            }
        }
    }

    /**
     * Sees if we are in the process of syncing added/edited moods to elastic search
     *
     * @return boolean that checks if we are finished syncing
     */
    public boolean getAddCompletion() {
        return addCompletion;
    }

    /**
     * Sees if we are in the process of syncing deleted moods to elastic search
     *
     * @return boolean that checks if we are finished syncing
     */
    public boolean getDeleteCompletion() {
        return deleteCompletion;
    }

    /**
     * Sets latitude.
     *
     * @param lat the latitude
     */
    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    /**
     * Sets longitude.
     *
     * @param lon the longitude
     */
    public void setLongitude(double lon) {
        this.longitude = lon;
    }

    /**
     * Deletes a mood both locally from the array list on the controller and on elastic search
     *
     * @param position position of the mood in the list to delete
     */
    public void deleteMood(int position) {

        Mood m = moodHistoryList.get(position);
        instance.moodHistoryList.remove(position);

        if (m.getId() != null) {
            //DeleteSyncTask deleteSyncTask = new DeleteSyncTask(m);
            deleteSyncList.add(m);
        } else {
            // it is not on elastic search yet, therefore we update locally
            Date date = m.getDate();
            for (int i = 0; i < addSyncList.size(); i++) {
                if (addSyncList.get(i).getDate() == date) {
                    addSyncList.remove(i);
                    break;
                }
            }
        }

    }

    /**
     * Gets the moods by calling getMoodTask.execute() to get moods from elastic search
     *
     *
     * @param userList    the list of users who we want the retrieved moods to belong to
     * @param tempRefresh the temp refresh
     * @return a list of moods
     */
    public ArrayList<Mood> getMoodList(ArrayList<String> userList, boolean tempRefresh) {

        this.refresh = tempRefresh;
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

    /**
     * Sets filter emotion.
     *
     * @param emotions     array list of emotions
     * @param listType boolean that determines which arraylist we use
     */
// sets the emotion to filter for
    public void setFilterEmotion(ArrayList<Integer> emotions, boolean listType) {
        if(listType) {
            queryBuilder.setEmotion(emotions);
        } else {
            followQueryBuilder.setEmotion(emotions);
        }
    }

    /**
     * Sets filter recent.
     *
     * @param recent       the recent
     * @param listType boolean that determines which arraylist we use
     */
// set to true if we want moods from last seven days
    public void setFilterRecent(boolean recent, boolean listType) {
        if(listType) {
            queryBuilder.setRecent(recent);
        } else {
            followQueryBuilder.setRecent(recent);
        }
    }

    /**
     * Sets filter text.
     *
     * @param reasonText   the reason text
     * @param listType boolean that determines which arraylist we use
     */
    public void setFilterText(String reasonText, boolean listType) {
        if(listType) {
            queryBuilder.setReason(reasonText);
        } else {
            followQueryBuilder.setReason(reasonText);
        }
    }

    /**
     * Clear filter text.
     *
     * @param listType boolean that determines which arraylist we use
     */
    public void clearFilterText(boolean listType) {
        if(listType) {
            queryBuilder.clearReason();
        } else {
            followQueryBuilder.clearReason();
        }
    }

    /**
     * Clear emotion.
     *
     * @param listType boolean that determines which arraylist we use
     */
    public void clearEmotion(boolean listType) {
        if (listType) {
            queryBuilder.clearEmotion();
        }
        else {
            followQueryBuilder.clearEmotion();
        }
    }

    public Mood getMood() {
        return tempMood;
    }

    public void setMood(Mood mood) {
        tempMood = mood;
    }

    /* ---------- Elastic Search Requests ---------- */


    /**
     * Sync add list on to elastic search.
     */
    public void syncAddList() {

        if (addSyncList.size() > 0) {
            addCompletion = false;
            AddMoodTask addMoodTask = new AddMoodTask();
            addMoodTask.execute(addSyncList);
        } else {
            addCompletion = true;
        }
    }

    /**
     * Sync delete list on to elastic search.
     */
    public void syncDeleteList() {

        if (deleteSyncList.size() > 0) {
            addCompletion = false;
            DeleteMoodTask deleteMoodTask = new DeleteMoodTask();
            deleteMoodTask.execute(deleteSyncList);
        }
    }


    /**
     * Async task that adds a moods to elastic search
     */
    private static class AddMoodTask extends AsyncTask<ArrayList<Mood>, Void, Integer> {
        // return value is the number of moods that succeeded
        @Override
        protected Integer doInBackground(ArrayList<Mood>... moods) {
            verifySettings();

            ArrayList<Mood> moodList = moods[0];
            int count = moodList.size();

            ArrayList<Index> bulkAction = new ArrayList<>();

            for (Mood mood : moodList) {
                bulkAction.add(new Index.Builder(mood).build());
            }

            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("cmput301w17t20").defaultType("mood")
                    .addAction(bulkAction)
                    .build();

            try {
                BulkResult result = client.execute(bulk);
                if (result.isSucceeded()) {
                    List<BulkResult.BulkResultItem> items = result.getItems();
                    // update JestId on locallist
                    int localIndex = 0;
                    int resultSize = items.size();
                    // starting from the last item of the result
                    // assumption that items on the leftmost index (start at 0)
                    // has no id as we just added them
                    for (int i = (resultSize - 1); i >= 0; i--) {
                        // http response 201, creation of document
                        // and local has no JestID
                        BulkResult.BulkResultItem item = items.get(i);
                        if ((item.status == 201) && (moodHistoryList.get(localIndex).getId() == null)) {
                            // check if mood corresponding to i (the one sent to elastic search)
                            // pass filters (so they have the same date as local)
                            if (moodHistoryList.get(localIndex).getDate().equals(moodList.get(i).getDate()) ) {
                                // if so, update JestId locally
                                String jestId = item.id;
                                moodHistoryList.get(localIndex).setId(jestId);
                                //increment local index
                                localIndex += 1;
                            }
                        }
                    }

                } else {
                    Log.i("Error", "The application failed to  send the moods");

                }
            } catch (Exception e) {
                Log.i("Error", "The application failed to build and send the moods");

                addCompletion = true;
                return count;
            }

            // in case we add more elements to the list
            // remove only the moods successfully syncs
            for (int j = 0; j < count; j++) {
                addSyncList.remove(0);
            }

            addCompletion = true;
            return count;
        }
    }


    /**
     * Async task that deletes a mood from elastic search
     */
    private static class DeleteMoodTask extends AsyncTask<ArrayList<Mood>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<Mood>... moods) {
            verifySettings();

            ArrayList<Mood> moodList = moods[0];
            int count = moodList.size();
            ArrayList<Delete> bulkAction = new ArrayList<>();

            for (Mood mood : moodList) {
                bulkAction.add(new Delete.Builder(mood.getId()).build());
            }

            Bulk bulk = new Bulk.Builder()
                    .defaultIndex("cmput301w17t20").defaultType("mood")
                    .addAction(bulkAction)
                    .build();

            try {
                BulkResult result = client.execute(bulk);
                if (!result.isSucceeded()) {
                    Log.i("Error", "The application failed to delete the moods");
                }
            } catch (Exception e) {
                Log.i("Error", "The application failed to build and delete the moods");
                deleteCompletion = true;
                return false;
            }

            // following code deletes comments associated with the deleted moods
            boolean commentsDeleted = false;

            String query = "{\n" +
                    "\t\"query\": {\n" +
                    "\t\t\"query_string\" : { \n" +
                    "\t\t\t\"fields\" : [\"moodId\"],\n" +
                    "\t\t\t\"query\" : \"";

            String queryMiddle = moodList.get(0).getId();

            for (int i = 1; i < moodList.size(); i++) {
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
                deleteCompletion = true;
            }

            if (commentsDeleted) {
                // in case we add more elements to the list
                for (int j = 0; j < count; j++) {
                    deleteSyncList.remove(0);
                }
            }

            deleteCompletion = true;

            return true;
        }
    }

    /**
     * Async task that gets an arraylist of moods from elastic search
     */
    private static class GetMoodTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Mood>> {
        // constants that define which list to use
        final static int HISTORY = 0;
        final static int FOLLOWING = 1;
        final static int NEARBY = 2;
        @Override
        protected ArrayList<Mood> doInBackground(ArrayList<String>... search_parameters) {

            verifySettings();
            ArrayList<String> usernames = search_parameters[0];
            if (usernames.size() == 0) {
                return new ArrayList<>();
            }

            //
            int historyMoods = FOLLOWING;

            if ((usernames.size() == 1) && (usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {
                historyMoods = HISTORY;
            }

            // for nearby moods
            if ((usernames.size() > 1) && (usernames.get(0) == UserController.getInstance().getCurrentUser().getName())) {
                historyMoods = NEARBY;
            }

            String query = "";

            // for history mood list
            if (historyMoods == HISTORY) {

                if(refresh) {
                    queryBuilder.setResultOffset(0);
                    moodHistoryList.clear();
                } else {
                    queryBuilder.setResultOffset(moodHistoryList.size());
                }
                queryBuilder.setUsers(usernames);
                query = queryBuilder.getMoodQuery();

            // for following mood list
            } else if (historyMoods == FOLLOWING){

                if(refresh) {
                    followQueryBuilder.setResultOffset(0);
                    moodFollowList.clear();
                } else {
                    followQueryBuilder.setResultOffset(moodFollowList.size());
                }
                followQueryBuilder.setUsers(usernames);
                query = followQueryBuilder.getMoodQuery();

            } else if (historyMoods == NEARBY) {
                query = "{ \n" +
                        "\t\"from\" : 0, \"size\" : 10\n" +
                        "\t, \"filter\" : {\n" +
                        "                \"geo_distance_range\" : {\n" +
                        "                    \"from\" : \"0km\",\n" +
                        "                    \"to\" : \"5km\",\n" +
                        "                    \"geo_location\" : {\n" +
                        "                        \"lat\" : " + Double.toString(latitude) +  ",\n" +
                        "                        \"lon\" : " + Double.toString(longitude) + "\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            }\n" +
                        "    , \"sort\": { \"date\": { \"order\": \"desc\" } } \n" +
                        "} ";
            }

            ArrayList<Mood> nearbyMoods = new ArrayList<Mood>();
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
                    if (historyMoods == HISTORY) {
                        for (int i = 0; i < foundMoods.size(); i++) {
                            Mood temp = foundMoods.get(i).source;
                            moodHistoryList.add(temp);
                        }
                    } else if (historyMoods == FOLLOWING){
                        for (int i = 0; i < foundMoods.size(); i++) {
                            Mood temp = foundMoods.get(i).source;
                            moodFollowList.add(temp);
                        }
                    } else {
                        for (int i = 0; i < foundMoods.size(); i++) {
                            Mood temp = foundMoods.get(i).source;
                            nearbyMoods.add(temp);
                        }
                        moodNearbyList = nearbyMoods;
                    }
                } else {
                    Log.i("Error", "Search query failed to find any moods that matched");
                }
            } catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }

            if (historyMoods == HISTORY) {
                return moodHistoryList;
            } else if (historyMoods == FOLLOWING){
                return moodFollowList;
            } else {
                return nearbyMoods;
            }
        }
    }

    /* ---------- Getters ---------- */

    /**
     * Gets a list of history moods.
     *
     * @return arraylist of history moods
     */
    public ArrayList<Mood> getHistoryMoods() {
        return moodHistoryList;
    }

    /**
     * Gets a list of following moods.
     *
     * @return arraylist of following moods
     */
    public ArrayList<Mood> getFollowMoods() {
        return moodFollowList;
    }

}