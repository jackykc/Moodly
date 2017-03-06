package com.example.moodly;

import java.util.ArrayList;

/**
 * Created by MinhNguyen on 06/03/2017.
 */

public class MoodController {
    private static final MoodController instance = new MoodController();
    ArrayList<Mood> moodList;

    private MoodController() {
        moodList = new ArrayList<Mood>();
    }

    public static MoodController getInstance() {
        return instance;
    }

    private void addMood(Mood m){
        instance.moodList.add(m);
    }

    private void editMood (int position, Mood newMood) {
        moodList.remove(position);
        moodList.add(position, newMood);
    }

    private void deleteMood(int position) {
        moodList.remove(position);
    }

    private String getLocation(int position) {
        Mood m = moodList.get(position);
        return m.getLocation();
    }

    private ArrayList<Mood> filer(){
        return moodList;
    }
}
