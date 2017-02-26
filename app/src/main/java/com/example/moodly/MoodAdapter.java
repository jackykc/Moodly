package com.example.moodly;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class MoodAdapter {
    public ArrayList<Mood> moodList = new ArrayList<>();
    private MoodHolder holder;


    public void getView(int position, View convertView, ViewGroup parent) {

    }

    private void setupItem(MoodHolder holder) {
        holder.emotion.setText(holder.mood.getEmotion());
        holder.date.setText((CharSequence) holder.mood.getDate());
        holder.username.setText(holder.mood.getOwner());
    }


}