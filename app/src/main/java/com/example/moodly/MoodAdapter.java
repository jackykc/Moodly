package com.example.moodly;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.List;

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
public class MoodAdapter extends ArrayAdapter<Mood> {

    private ArrayList<Mood> items;
    private int layoutResourceId;
    private MoodHolder holder;
    private Context context;

    /**
     * Constructor for our MoodAdapter
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public MoodAdapter(Context context, int layoutResourceId, ArrayList<Mood> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    /**
     * Gets the view in which to setup the row of our custom list
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

        holder.emotion = (TextView) row.findViewById(R.id.mood_emotion);
        holder.date = (TextView) row.findViewById(R.id.mood_date);

        setupItem(holder);

        return row;
    }

    /**
     * Sets up the XML elements in our MoodHolder
     * @param holder
     */
    private void setupItem(MoodHolder holder) {
        String emotionString = toStringEmotion(holder.mood.getEmotion());
        holder.emotion.setText(emotionString);
        holder.date.setText(holder.mood.getOwner());
    }

    /**
     * From our emotion enum, return the string representation of it
     * @param emotion an enum of emotions
     * @return a string repesentation of our emotion
     */
    private String toStringEmotion(int emotion) {
        switch (emotion) {
            case 1:
                return "Anger";
            case 2:
                return "Confusion";
            case 3:
                return "Disgust";
            case 4:
                return "Fear";
            case 5:
                return "Happiness";
            case 6:
                return "Sadness";
            case 7:
                return "Shame";
            case 8:
                return "Suprise";
            default:
                return "None";
        }
    }


}