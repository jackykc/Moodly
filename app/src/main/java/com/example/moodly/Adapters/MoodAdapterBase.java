package com.example.moodly.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.moodly.Models.Emotion;
import com.example.moodly.Models.Mood;
import com.example.moodly.R;

import java.util.ArrayList;

/**
 * Created by jkc1 on 2017-04-03.
 */

public abstract class MoodAdapterBase extends ArrayAdapter<Mood> {
    protected ArrayList<Mood> items;
    protected int layoutResourceId;
    protected Context context;

    /**
     * Constructor for our MoodAdapter
     * @param context
     * @param layoutResourceId resource id for our single list item
     * @param items ArrayList of moods
     */
    public MoodAdapterBase(Context context, int layoutResourceId, ArrayList<Mood> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    /**
     * Abstract class gets the view for the holder
     * @param position position of the item in the array
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getView(int position, View convertView, ViewGroup parent);

    /**
     * Sets up the XML elements in our MoodHolder
     * @param holder
     */
    protected void setupItem(MoodHolder holder) {
        String emotionString = toStringEmotion(holder.mood.getEmotion());
        holder.emotion.setText(emotionString);
        holder.date.setText(holder.mood.getDate().toString());
        emotionToEmoji(holder.emoji, holder.mood.getEmotion());
    }

    protected void setBackground(MoodHolder holder, View row) {
        if (holder.mood.getEmotion() == Emotion.ANGER) {row.setBackgroundColor(Color.parseColor("#f1646c"));}
        if (holder.mood.getEmotion() == Emotion.CONFUSION) {row.setBackgroundColor(Color.parseColor("#7971b4"));}
        if (holder.mood.getEmotion() == Emotion.DISGUST) {row.setBackgroundColor(Color.parseColor("#9dd5c0"));}
        if (holder.mood.getEmotion() == Emotion.FEAR) {row.setBackgroundColor(Color.parseColor("#fac174"));}
        if (holder.mood.getEmotion() == Emotion.HAPPINESS) {row.setBackgroundColor(Color.parseColor("#fff280"));}
        if (holder.mood.getEmotion() == Emotion.SADNESS) {row.setBackgroundColor(Color.parseColor("#27a4dd"));}
        if (holder.mood.getEmotion() == Emotion.SHAME) {row.setBackgroundColor(Color.parseColor("#f39cc3"));}
        if (holder.mood.getEmotion() == Emotion.SURPRISE) {row.setBackgroundColor(Color.parseColor("#FFFFFF"));}
    }

    /**
     * From our emotion enum, set drawable emoji to imageview
     * @param emotion an enum of emotions
     * @param emoji is the imageview that holds the emoji
     */
    protected void emotionToEmoji (ImageView emoji, int emotion){
        switch (emotion) {
            case Emotion.ANGER:
                emoji.setImageResource(R.drawable.angry);
                break;
            case Emotion.CONFUSION:
                emoji.setImageResource(R.drawable.confused);
                break;
            case Emotion.DISGUST:
                emoji.setImageResource(R.drawable.disgust);
                break;
            case Emotion.FEAR:
                emoji.setImageResource(R.drawable.afraid);
                break;
            case Emotion.HAPPINESS:
                emoji.setImageResource(R.drawable.happy);
                break;
            case Emotion.SADNESS:
                emoji.setImageResource(R.drawable.sad);
                break;
            case Emotion.SHAME:
                emoji.setImageResource(R.drawable.shame);
                break;
            case Emotion.SURPRISE:
                emoji.setImageResource(R.drawable.surprise);
                break;
            default:
                break;
        }
    }

    /**
     * From the emotion enum, return the string representation of it
     * @param emotion
     * @return String name of an emotion
     */
    protected String toStringEmotion(int emotion) {
        switch (emotion) {
            case Emotion.ANGER:
                return "Anger";
            case Emotion.CONFUSION:
                return "Confusion";
            case Emotion.DISGUST:
                return "Disgust";
            case Emotion.FEAR:
                return "Fear";
            case Emotion.HAPPINESS:
                return "Happiness";
            case Emotion.SADNESS:
                return "Sadness";
            case Emotion.SHAME:
                return "Shame";
            case Emotion.SURPRISE:
                return "Surprise";
            default:
                return "None";
        }
    }


}
