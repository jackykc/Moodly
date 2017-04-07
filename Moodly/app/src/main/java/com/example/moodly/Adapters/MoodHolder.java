package com.example.moodly.Adapters;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.moodly.Models.Mood;

/**
 * Created by mliew on 2017-02-25.
 */

/**
 * MoodHolder holds a mood and it's relevant attributes.
 */
public class MoodHolder {
    Mood mood;
    TextView emotion;
    TextView date;
    TextView username;
    TextView reasonPicture;
    ImageView emoji;
}
