package com.example.moodly;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class MoodTest extends ActivityInstrumentationTestCase2{
    public MoodTest() {
        super(MoodHolder.class);
    }

    public void testAddMood(){
        ArrayList<Mood> moodList = new ArrayList<>();
        Mood mood = new Mood();
        String emotion = "Happy";
        String owner = "Harambe";
        String trigger = "Banana";
        String reasonText = "Ate a banana";
        mood.setOwner(owner);
        mood.setEmotion(emotion);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);
        moodList.add(mood);
        assertTrue(moodList.contains(mood));
    }

    public void testDeleteMood(){
        ArrayList<Mood> moodList = new ArrayList<>();
        Mood mood = new Mood();
        String emotion = "Happy";
        String owner = "Harambe";
        String trigger = "Banana";
        String reasonText = "Ate a banana";
        mood.setOwner(owner);
        mood.setEmotion(emotion);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);
        moodList.add(mood);
        moodList.remove(mood);
        assertFalse(moodList.contains(mood));
    }
}
