package com.example.moodly;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.example.moodly.Activities.LoginScreen;
import com.example.moodly.Activities.ViewMood;
import com.example.moodly.Activities.ViewMoodList;
import com.robotium.solo.Solo;
import junit.framework.TestCase;

/**
 * Created by tuongmin on 3/16/17.
 */

public class AddMoodActivityTest extends ActivityInstrumentationTestCase2<LoginScreen> {
    private Solo solo;

    public AddMoodActivityTest() {
        super(com.example.moodly.Activities.LoginScreen.class);
    }

    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    public void testAddMood() {
        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);

        solo.enterText((EditText) solo.getView(R.id.userName), "Minh");

        solo.clickOnButton("Login");

        solo.assertCurrentActivity("Wrong Activities", ViewMoodList.class);

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));

        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);


    }

}
