package com.example.moodly;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.example.moodly.Activities.ViewMood;
import com.example.moodly.Activities.ViewMoodList;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

/**
 * Created by tuongmin on 3/16/17.
 */

public class AddMoodActivityTest extends ActivityInstrumentationTestCase2<ViewMoodList> {
    private Solo solo;

    public AddMoodActivityTest() {
        super(com.example.moodly.Activities.ViewMoodList.class);
    }

    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    public void testAddMood() {
        solo.assertCurrentActivity("Wrong Activity", ViewMoodList.class);

        solo.clickOnImageButton(R.id.fab);
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);

    }

}
