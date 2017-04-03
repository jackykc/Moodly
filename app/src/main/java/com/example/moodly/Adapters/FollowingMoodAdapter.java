package com.example.moodly.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */
/*
* check jkc1-SizeBook's RecordListAdapter
* for references
*
* */

/**
 * Custom adapter to adapt moods onto a listview
 */
public class FollowingMoodAdapter extends MoodAdapterBase {

    /**
     * Constructor for our FollowingMoodAdapter
     *
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items            ArrayList of moods
     */
    public FollowingMoodAdapter(Context context, int layoutResourceId, ArrayList<Mood> items) {
        super(context, layoutResourceId, items);
    }

    /**
     * Gets the view in which to setup the row of our custom list
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        MoodHolder holder = new MoodHolder();
        holder.mood = items.get(position);

        holder.emotion = (TextView) row.findViewById(R.id.f_mood_emotion);
        holder.date = (TextView) row.findViewById(R.id.f_mood_date);
        holder.emoji = (ImageView) row.findViewById(R.id.f_emoji);
        holder.username = (TextView) row.findViewById(R.id.f_mood_owner);

        setupItem(holder);

        setBackground(holder, row);


        return row;
    }

    /**
     * Sets up the XML elements in our MoodHolder
     *
     * @param holder
     */
    @Override
    protected void setupItem(MoodHolder holder) {
        String emotionString = toStringEmotion(holder.mood.getEmotion());
        holder.emotion.setText(emotionString);
        holder.date.setText(holder.mood.getDate().toString());
        holder.username.setText(holder.mood.getOwner());
        emotionToEmoji(holder.emoji, holder.mood.getEmotion());
    }

}