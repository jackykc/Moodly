package com.example.moodly.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.moodly.Controllers.CommentController;
import com.example.moodly.Models.Comment;
import com.example.moodly.R;

import java.util.ArrayList;

public class ViewComments extends AppCompatActivity {
    ArrayList<Comment> commentList = new ArrayList<>();
    ListView displayCommentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        String id = getIntent().getStringExtra("moodID");
        commentList = CommentController.getInstance().getCommentList(id);
        showComments(commentList);
    }

    protected void showComments(ArrayList<Comment> commentList){
        if (commentList.size() == 0){
            // if there isn't any comments, print message to tell user?
        }
        else {
            displayCommentList = (ListView) findViewById(R.id.commentsView);
            ArrayAdapter<Comment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentList);
            displayCommentList.setAdapter(adapter);
        }
    }

}
