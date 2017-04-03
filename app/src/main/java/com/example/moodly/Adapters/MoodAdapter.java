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

import com.example.moodly.Models.Emotion;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

/**
 * Custom adapter to adapt moods onto a ListView
 */
public class MoodAdapter extends MoodAdapterBase {

    /**
     * Constructor for our MoodAdapter
     * @param context the current state of the application
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public MoodAdapter(Context context, int layoutResourceId, ArrayList<Mood> items) {
        super(context, layoutResourceId, items);
    }

    /**
     * Gets the view in which to setup the row of our custom list
     * @param position
     * @param convertView
     * @param parent
     * @return the row that the mood event is shown
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        MoodHolder holder = new MoodHolder();
        holder.mood = items.get(position);

        holder.emotion = (TextView) row.findViewById(R.id.mood_emotion);
        holder.date = (TextView) row.findViewById(R.id.mood_date);
        holder.emoji = (ImageView) row.findViewById(R.id.emoji);

        setupItem(holder);

        setBackground(holder,row);

        return row;
    }

}