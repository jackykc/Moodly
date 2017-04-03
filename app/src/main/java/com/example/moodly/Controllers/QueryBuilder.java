package com.example.moodly.Controllers;

import com.example.moodly.Models.Mood;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-03-19.
 */
public class QueryBuilder {

    private String usernameString;

    private ArrayList<Integer> emotionList;
    private boolean recent;
    private String reason;
    private int resultOffset;


    /**
     * Instantiates a new Query builder.
     */
    public QueryBuilder() {

        usernameString = "";

        recent = false;
        reason = "";
        resultOffset = 0;
        emotionList = new ArrayList<Integer>();

    }

    /**
     * Checks if a mood is still kept after  going through the filter
     *
     * @param m the mood
     * @return true if mood does not get filtered out, false otherwise
     */
    public boolean withinFilter(Mood m) {
        // mood's emotion does not match states emotion
        if((emotionList.size() > 0 ) && (! emotionList.contains(m.getEmotion()))) {
            return false;
        // reason text does not match
        } else if ((reason != "") && (! m.getReasonText().contains(reason))) {
            return false;
        }
        return true;
    }

    /**
     * Sets the offset of the elastic search results
     *
     * @param resultOffset the result offset
     */
    public void setResultOffset(int resultOffset) {
        this.resultOffset = resultOffset;
    }

    /**
     * Sets an arraylist emotion to filter for.
     *
     * @param emotions arraylist of emotions
     */
    public void setEmotion(ArrayList<Integer> emotions) {
        this.emotionList = emotions;
    }

    /**
     * Sets recent, boolean to check for recent moods.
     *
     * @param recent boolean, true to filter for recent moods
     */
    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    /**
     * Sets reason text to filter for.
     *
     * @param reason the reason text
     */
// Assuming our reason text search is a single word
    // sets the reason to search for
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Clears emotion list.
     */
    public void clearEmotion() {emotionList.clear();}

    /**
     * Clears reason text.
     */
    public void clearReason() {reason = "";}

    /**
     * Sets user string to query for.
     *
     * @param usernameArray array of usernames
     */
    public void setUsers(ArrayList<String> usernameArray) {

        usernameString = usernameArray.get(0);
        for (int i = 1; i < usernameArray.size(); i++) {
            usernameString += " OR ";
            usernameString += usernameArray.get(i);
        }

    }


    /**
     * Gets mood query.
     *
     * @return string representing query for a mood
     */
    public String getMoodQuery() {

        String emotionMatch, recentMatch, reasonMatch;
        String emotionString;

        // for mood owner
        String ownerMatch = "\"must\" : { \n" +
                "\"query_string\" : { \n" +
                "\"fields\" : [\"owner\"],\n" +
                "\"query\" : \"" + usernameString + "\"\n" +
                "}" +
                "\n}";

        // sort by most recent
        String sort = "\n\"sort\": { \"date\": { \"order\": \"desc\" } }";

        // start of the query
        String query =
                "{ \n" +
                        "\t\"from\" : "+Integer.toString(resultOffset)+", \"size\" : 10,\n" +
                        "\n\"query\" : {\n" +
                    "\"bool\" : {\n";

        query += ownerMatch;

        // for emotions
        if(emotionList.size() > 0) {
            // string that represents emotions "[1, 3, 5...]"
            emotionString = "[";
            emotionString += emotionList.get(0).toString();
            for (int i = 1; i < emotionList.size(); i++) {
                emotionString += ", ";
                emotionString += emotionList.get(i).toString();
            }
            emotionString += "]";


            emotionMatch = ",\"must\" : { \n" +
                        "\"terms\" : { \n" +
                            "\"emotion\" : " + emotionString +
                        "\n}" +
                    "\n}";
            query += emotionMatch;
        }

        // for moods on the current week
        if(recent) {
            recentMatch = ",\"must\" : { \n" +
                    "\"range\" : { \n" +
                    "\"date\" : {\n" +
                    "\"gte\" : \"now/w\"" +
                    "\n}" +
                    "\n}" +
                    "\n}";
            query += recentMatch;
        }

        // for filter by reason text
        if(!reason.isEmpty()) {
            reasonMatch = ",\"must\" : { \n" +
                    "\"query_string\" : { \n" +
                    "\"fields\" : [\"reasonText\"],\n" +
                    "\"query\" : \"" + reason + "\"\n" +
                    "}" +
                    "\n}";
            query += reasonMatch;
        }

        query += "\n}" + // ends bool
                "\n},"; // ends query

        query += sort;

        query += " \n} "; // ends first bracket

        return query;
    }
}
