package com.example.moodly.Controllers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.moodly.Models.Comment;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by jkc1 on 2017-03-20.
 * Controller to access a mood's comments
 */
public class CommentController extends ElasticSearchController {

    private static CommentController instance = null;
    private static ArrayList<Comment> commentList = new ArrayList<>();
    private static String owner;
    private static boolean refresh;

    private CommentController() {

        commentList = new ArrayList<>();
        owner = UserController.getInstance().getCurrentUser().getName();
        refresh = true;

    }

    /**
     * Gets instance of the comment controller.
     * If it is null, create a new instance.
     *
     * @return the instance
     */
    public static CommentController getInstance() {
        if(instance == null) {
            instance = new CommentController();
        }
        return instance;

    }

    /* ---------- Controller Functions ---------- */
    /**
     * Adds a comment to a mood.
     *
     * @param text   the comment text
     * @param moodID the mood id the comment is associated with
     */

    public static void addComment(String text, String moodID) {
        Comment tempComment = new Comment(text, owner, moodID);
        // add to local list for display
        commentList.add(0, tempComment);
        CommentController.AddCommentTask addCommentTask = new CommentController.AddCommentTask();
        addCommentTask.execute(tempComment);
    }

    /**
     * Gets a list of comments from a mood
     *
     * @param moodID      the mood id
     * @param tempRefresh true for refreshing comments, false for loading more
     * @return tempCommentList a list of comments associated with the moodID
     */
    public ArrayList<Comment> getCommentList (String moodID, boolean tempRefresh) {

        refresh = tempRefresh;
        CommentController.GetCommentTask getCommentTask = new CommentController.GetCommentTask();
        getCommentTask.execute(moodID);

        ArrayList<Comment> tempCommentList = new ArrayList<Comment>();
        try {
            tempCommentList = getCommentTask.get();
        } catch (Exception e) {

            Log.i("Error", "Failed to get comments out of async object");
        }

        return tempCommentList;
    }

    /* ---------- Elastic Search Requests ---------- */

    /**
     * Async task that adds comments onto elastic search based
     * on the moodID.
     */
    private static class AddCommentTask extends AsyncTask<Comment, Void, Void> {

        @Override
        protected Void doInBackground(Comment... comments){
            verifySettings();

            for(Comment comment : comments) {

                Index index = new Index.Builder(comment).index("cmput301w17t20").type("comment").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        if (comment.getId() == null) {
                            comment.setId(result.getId());
                            // assumption method addComment always runs before this
                            // if the id is not set, set it
                            if(commentList.get(0).getId() == null) {
                                commentList.get(0).setId(result.getId());
                            }
                        }
                    } else {
                        Log.i("Error", "Elasticsearch was not able to add the comment");
                    }
                }
                catch (Exception e) {
                    Log.i("Error", "The application failed to build and send the comment");
                }
            }
            return null;
        }
    }


    /**
     * Async task that gets comments from elastic search.
     * @return commentList the result from the getting comments from ElasticSearch
     */
    private static class GetCommentTask extends AsyncTask<String, Void, ArrayList<Comment>> {
        @Override
        protected ArrayList<Comment> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<Comment> currentCommentList = new ArrayList<Comment>();


            String moodId = search_parameters[0];
            // use query builder to find comments with moodID
            String query;
            if(refresh) {
                query = "{\n" +
                        "\t\"query\": {\n" +
                        "\t\t\"match\": {\n" +
                        "\t\t\t\"moodId\": \"" + moodId + "\" }\n" +
                        "\t}\n" +
                        "\n,\"sort\": { \"date\": { \"order\": \"desc\" } }\n" +
                        "}";

            } else {
                query = "{ \n" +
                        "\t\"from\" : " + Integer.toString(commentList.size()) + ", \"size\" : 10,\n" +
                        "\t\"query\": {\n" +
                        "\t\t\"match\": {\n" +
                        "\t\t\t\"moodId\": \"" + moodId + "\" }\n" +
                        "\t}\n" +
                        "\n,\"sort\": { \"date\": { \"order\": \"desc\" } }\n" +
                        "}";
            }


            Search search = new Search.Builder(query)
                    .addIndex("cmput301w17t20")
                    .addType("comment")
                    .build();

            try {
                // get the results of our query
                SearchResult result = client.execute(search);
                if(result.isSucceeded()) {
                    // hits
                    List<SearchResult.Hit<Comment, Void>> foundComments = result.getHits(Comment.class);

                    if(refresh) {
                        for (int i = 0; i < foundComments.size(); i++) {
                            Comment temp = foundComments.get(i).source;
                            currentCommentList.add(temp);

                        }
                        commentList = currentCommentList;
                    } else {
                        for (int i = 0; i < foundComments.size(); i++) {
                            Comment temp = foundComments.get(i).source;
                            commentList.add(temp);

                        }
                    }
                } else {
                    Log.i("Error", "Search query failed to find any comments that matched");
                }
            }
            catch (Exception e) {
                Log.i("Error", "Something went wrong when we tried to communicate with the elasticsearch server!");
            }
            return commentList;
        }
    }


}

