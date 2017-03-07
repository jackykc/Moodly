package com.example.moodly;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MinhNguyen on 06/03/2017.
 */

public class MoodController {
    private static final MoodController instance = new MoodController();
    protected static ArrayList<Mood> moodList;

    private MoodController() {
        moodList = new ArrayList<Mood>();
    }

    public static MoodController getInstance() {
        return instance;
    }

    protected void addMood(Mood m){
        instance.moodList.add(m);
    }

    protected void editMood (int position, Mood newMood) {
        moodList.remove(position);
        moodList.add(position, newMood);
    }

    protected void deleteMood(int position) {
        moodList.remove(position);
    }

    protected String getLocation(int position) {
        Mood m = moodList.get(position);
        return m.getLocation();
    }

    protected ArrayList<Mood> filterByDate(Date startDate, Date endDate) {
        ArrayList<Mood> result = new ArrayList<>();
        for (Mood m: moodList) {
            if (m.getDate().after(startDate) && m.getDate().before(endDate)){
                result.add(m);
            }
        }
        return result;
    }

    protected ArrayList<Mood> filterByEmoState(Emotion e) {
        ArrayList<Mood> result = new ArrayList<>();
        for (Mood m: moodList) {
            if (m.getEmotion().equals(e)){
                result.add(m);
            }
        }
        return  result;
    }

    protected ArrayList<Mood> filterByTextReason(String reason) {
        ArrayList<Mood> result = new ArrayList<>();
        for(Mood m:moodList) {
            if (m.getReasonText().contains(reason)) {
                result.add(m);
            }
        }
        return result;
    }
}
