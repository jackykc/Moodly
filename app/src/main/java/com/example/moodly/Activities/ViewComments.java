package com.example.moodly.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.moodly.Controllers.CommentController;
import com.example.moodly.Models.Comment;
import com.example.moodly.R;

import java.util.ArrayList;

public class ViewComments extends AppCompatActivity {
    ArrayList<Comment> commentList = new ArrayList<>();
    ListView displayCommentList;
    TextView displayNoComment;
    Button loadMoreComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);
        android.support.v7.app.ActionBar action = getSupportActionBar();
        action.setTitle("Comments");
        String id = getIntent().getStringExtra("moodID");
        commentList = CommentController.getInstance().getCommentList(id);
        showComments(commentList);
        setColor();
    }

    protected void showComments(ArrayList<Comment> commentList){
        ViewSwitcher viewSwitcher = (ViewSwitcher)findViewById(R.id.messageSwitcher);
        displayCommentList = (ListView) findViewById(R.id.commentsView);
        displayNoComment = (TextView) findViewById(R.id.noComments);
        loadMoreComments = (Button) findViewById(R.id.loadMore);
        if (commentList.size() == 0){
            viewSwitcher.showNext();
            loadMoreComments.setVisibility(Button.INVISIBLE);
        }
        else {
            ArrayAdapter<Comment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commentList);
            displayCommentList.setAdapter(adapter);
        }
    }

    protected void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    protected void setColor(){
        int mood = getIntent().getIntExtra("colorID", 0);

        if (mood == 1) {setActivityBackgroundColor(Color.parseColor("#E57373"));}
        if (mood == 2) {setActivityBackgroundColor(Color.parseColor("#BA68C8"));}
        if (mood == 3) {setActivityBackgroundColor(Color.parseColor("#4CAF50"));}
        if (mood == 4) {setActivityBackgroundColor(Color.parseColor("#FFA726"));}
        if (mood == 5) {setActivityBackgroundColor(Color.parseColor("#FFEE58"));}
        if (mood == 6) {setActivityBackgroundColor(Color.parseColor("#2196F3"));}
        if (mood == 7) {setActivityBackgroundColor(Color.parseColor("#F06292"));}
        if (mood == 8) {setActivityBackgroundColor(Color.parseColor("#FFFFFF"));}

    }

}
