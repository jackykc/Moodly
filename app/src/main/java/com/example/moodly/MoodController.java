//package com.example.moodly;
//
//import java.util.ArrayList;
//
///**
// * Created by MinhNguyen on 06/03/2017.
// */
//
//
//// https://www.youtube.com/watch?v=NZaXM67fxbs singleton design pattern Mar 06
//public class MoodController {
//    // this isn't safe from synchronization, does it need to be?
//    // i don't know how to verify that, but i guess we will find out
//    // soon
//    private static MoodController instance = null;
//    private ArrayList<Mood> moodList;
//    private ArrayList<Mood> moodFollowList;
//    private ArrayList<Mood> filteredList;
//
//    private MoodController() {
//        moodList = new ArrayList<Mood>();
//        moodFollowList = new ArrayList<Mood>();
//        filteredList = new ArrayList<Mood>();
//    }
//
//    public static MoodController getInstance() {
//
//        if(instance == null) {
//            instance = new MoodController();
//        }
//
//        return instance;
//    }
//
//    public void addMood(Mood m){
//        instance.moodList.add(m);
//    }
//
//    public void editMood (int position, Mood newMood) {
//        moodList.remove(position);
//        moodList.add(position, newMood);
//    }
//
//    public void deleteMood(int position) {
//        moodList.remove(position);
//    }
//
//
//    public String getLocation(int position) {
//        Mood m = moodList.get(position);
//        return m.getLocation();
//    }
//
//
//
//    public ArrayList<Mood> getFiltered() {
//        this.filter();
//        return this.filteredList;
//    }
//    // ??? make helper functions ?
//    // only filters and sets the filtered list as filteredList
//    // moodList should be the full list
//    // filtering person, good luck
//    // so, currently all this does is set filteredList to reference
//    // each of its elemnts from moodList, this is not a deep copy
//    private void filter(){
//        filteredList = (ArrayList<Mood>) moodList.clone();
//    }
//}
//
//


















package com.example.moodly;

        import android.os.AsyncTask;
        import android.util.Log;

        import com.searchly.jestdroid.DroidClientConfig;
        import com.searchly.jestdroid.JestClientFactory;
        import com.searchly.jestdroid.JestDroidClient;

        import java.util.ArrayList;
        import java.util.List;

        import io.searchbox.core.DocumentResult;
        import io.searchbox.core.Index;
        import io.searchbox.core.Search;
        import io.searchbox.core.SearchResult;
//import com.jest
/**
 * Created by MinhNguyen on 06/03/2017.
 */


// https://www.youtube.com/watch?v=NZaXM67fxbs singleton design pattern Mar 06
public class MoodController {
    // this isn't safe from synchronization, does it need to be?
    // i don't know how to verify that, but i guess we will find out
    // soon
    private static MoodController instance = null;
    private ArrayList<Mood> moodList;
    private ArrayList<Mood> moodFollowList;
    private ArrayList<Mood> filteredList;

    // move this out of the moodController
    private static JestDroidClient client;

    private MoodController() {
        moodList = new ArrayList<Mood>();
        moodFollowList = new ArrayList<Mood>();
        filteredList = new ArrayList<Mood>();
    }

    public static MoodController getInstance() {

        if(instance == null) {
            instance = new MoodController();
        }

        return instance;
    }

    public static class AddMoodTask extends AsyncTask<Mood, Void, Void> {

        @Override
        protected Void doInBackground(Mood... moods){
            verifySettings();

            for(Mood mood : moods) {
                // should probably create custom builder?
                String source = "{" +
                        "\"date\": " + mood.getDate().toString() + ", " +
                        "\"owner\": " + mood.getOwner() + ", " +
                        "\"location\": " + mood.getLocation() + ", " +
                        "\"trigger\": " + mood.getTrigger() + ", " +
                        "\"reasonText\": " + mood.getReasonText() + ", " +
                        "\"image\": " + mood.getImage() + ", " +
                        "\"emotion\": " + "2" + ", " +
                        "\"socialSituation\": " + "1" +
                        "}";

                Index index = new Index.Builder(source).index("cmput301w17t20").type("mood").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        mood.setId(result.getId());
                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the tweet");
                    }
                    // where is the client?
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the tweets");
                }

            }

            return null;
        }
    }
//
//    public static class GetTweetsTask extends AsyncTask<String, Void, ArrayList<Mood>> {
//        @Override
//        protected ArrayList<Mood> doInBackground(String... search_parameters) {
//            verifySettings();
//
//            ArrayList<Mood> currentMoodList = new ArrayList<Mood>();
//            String query =
//                    "{ \n\"query\" : {\n" +
//                            "    \"term\" : { \"message\" : \"" + search_parameters[0] +
//                            "\"     }\n " +
//                            "    }\n" +
//                            " } ";
//
//            // TODO Build the query
//            Search search = new Search.Builder(query)
//                    .addIndex("cmput301w17t20")
//                    .addType("mood")
//                    .build();
//
//            try {
//                // TODO get the results of the query
//                SearchResult result = client.execute(search);
//                if(result.isSucceeded()) {
//                    List<Mood> foundMoods = result.getSourceAsObjectList(Mood.class);
//                    currentMoodList.addAll(foundMoods);
//                } else {
//                    Log.i("Error", "Search query failed to find any tweets that matched");
//                }
//            }
//            catch (Exception e) {
//                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
//            }
//
//            return currentMoodList;
//        }
//    }


    public void addMood(Mood m){

        instance.moodList.add(m);
    }

    public void editMood (int position, Mood newMood) {
        moodList.remove(position);
        moodList.add(position, newMood);
    }

    public void deleteMood(int position) {
        moodList.remove(position);
    }


    public String getLocation(int position) {
        Mood m = moodList.get(position);
        return m.getLocation();
    }



    public ArrayList<Mood> getFiltered() {
        this.filter();
        return this.filteredList;
    }
    // ??? make helper functions ?
    // only filters and sets the filtered list as filteredList
    // moodList should be the full list
    // filtering person, good luck
    // so, currently all this does is set filteredList to reference
    // each of its elemnts from moodList, this is not a deep copy
    private void filter(){
        filteredList = (ArrayList<Mood>) moodList.clone();
    }

    // move this out of the mood controller?
    private static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://localhost:9200");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
