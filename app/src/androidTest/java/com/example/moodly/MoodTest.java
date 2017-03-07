package com.example.moodly;

import android.test.ActivityInstrumentationTestCase2;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mliew on 2017-02-25.
 */

public class MoodTest extends ActivityInstrumentationTestCase2 {
    public MoodTest() {
        super(MoodHolder.class);
    }

    MoodController con = MoodController.getInstance();
    ArrayList<Mood> moodList = con.moodList;

    public void testAddMood() {
        Mood mood = new Mood();
        Emotion emotion = Emotion.HAPPINESS;
        String owner = "Harambe";
        String trigger = "Banana";
        String reasonText = "Ate a banana";
        mood.setOwner(owner);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);
        mood.setEmotion(emotion);
        con.addMood(mood);
        assertTrue(moodList.contains(mood));
    }

    public void testChooseEmotionalState() {
        Mood mood = new Mood();
        Emotion emotion = Emotion.HAPPINESS;
        mood.setEmotion(emotion);
        con.addMood(mood);
        assertEquals(moodList.get(moodList.size() - 1).getEmotion(), emotion);
    }

    public void testAddTextReason() {
        Mood mood = new Mood();
        String textReason = "Happy";
        mood.setReasonText(textReason);
        con.addMood(mood);
        assertEquals(moodList.get(moodList.size() - 1).getReasonText(), textReason);
    }

    public void testAddLocation() {
        Mood mood = new Mood();
        String location = "Cincinnatti";
        mood.setLocation(location);
        con.addMood(mood);
        assertEquals(con.getLocation(moodList.size() - 1), location);
    }


    public void testViewMoodDetails() {
        Mood mood = new Mood();
        Emotion emotion = Emotion.ANGER;
        String owner = "Harambe";
        String trigger = "Banana";
        String reasonText = "Ate a banana";
        mood.setOwner(owner);
        mood.setEmotion(emotion);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);
        con.addMood(mood);
        assertEquals(emotion, moodList.get(moodList.size() - 1).getEmotion());
        assertEquals(trigger, moodList.get(moodList.size() - 1).getTrigger());
        assertEquals(reasonText, moodList.get(moodList.size() - 1).getReasonText());
    }

    public void testEditMood() {
        Mood mood = new Mood();
        Mood newMood = new Mood();

        Emotion emotion = Emotion.ANGER;
        String owner = "Harambe";
        String trigger = "Banana";
        String reasonText = "Ate a banana";

        mood.setOwner(owner);
        mood.setEmotion(emotion);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);

        con.addMood(mood);

        String newTrigger = "a kid";
        newMood.setEmotion(emotion);
        newMood.setOwner(owner);
        newMood.setTrigger(newTrigger);
        newMood.setReasonText(reasonText);

        con.editMood(0, newMood);

        assertEquals(emotion, moodList.get(0).getEmotion());
        assertEquals(newTrigger, moodList.get(0).getTrigger());
        assertEquals(reasonText, moodList.get(0).getReasonText());
    }

    public void testDeleteMood() {
        Mood mood = new Mood();
        Emotion emotion = Emotion.ANGER;
        String owner = "Harambe";
        String trigger = "Kid fell down";
        String reasonText = "Some1 shot me";

        mood.setOwner(owner);
        mood.setEmotion(emotion);
        mood.setTrigger(trigger);
        mood.setReasonText(reasonText);

        con.addMood(mood);
        con.deleteMood(moodList.size() - 1);
        assertFalse(moodList.contains(mood));
    }

    public void testFilter() {
        //TODO test filterByDate
        Mood mood = new Mood();
        Emotion emotion = Emotion.ANGER;
        String reason = "not happy";
        mood.setEmotion(emotion);
        mood.setReasonText(reason);
        con.addMood(mood);

        Mood anotherMood = new Mood();
        Emotion newEmotion = Emotion.CONFUSION;
        String newReason = "cant understand";
        anotherMood.setReasonText(newReason);
        anotherMood.setEmotion(newEmotion);
        con.addMood(anotherMood);

        ArrayList<Mood> result = con.filterByEmoState(Emotion.ANGER);
        assertEquals(result.get(0), mood);

        result = con.filterByTextReason("understand");
        assertEquals(result.get(0), anotherMood);
    }

}
