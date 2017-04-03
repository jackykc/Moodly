package com.example.moodly;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.moodly.Activities.LoginScreen;
import com.example.moodly.Activities.MoodBase;
import com.example.moodly.Activities.ViewMood;
import com.robotium.solo.Solo;

import java.util.Random;

/**
 * Created by tuongmin on 3/16/17.
 */


/*
* Testing Add, Edit, Delete, Filters with Moods
 */
public class MoodIntentTest extends ActivityInstrumentationTestCase2<LoginScreen> {
    private Solo solo;

    public MoodIntentTest() {
        super(com.example.moodly.Activities.LoginScreen.class);
    }

    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testStart() throws Exception {
        Activity activity = getActivity();
    }

    /*
    * Login to user "Minh"
     */
    public void actionLogin() {
        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);

        solo.enterText((EditText) solo.getView(R.id.userName), "Minh");

        solo.clickOnButton("Login");

        solo.assertCurrentActivity("Wrong Activities", MoodBase.class);
    }

    /*
    Log out of current user
     */
    public void actionLogOut() {
        solo.sendKey(solo.MENU);

        solo.clickOnText("Log Out");

        solo.assertCurrentActivity("Wrong Activity", LoginScreen.class);
    }

    /*
    Generate random strinf for test comment
     */
    protected String getRDString() {
        String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder stringBuilder = new StringBuilder();
        Random rnd = new Random();
        while (stringBuilder.length() < 18) {
            int index = (int) (rnd.nextFloat() * CHARS.length());
            stringBuilder.append(CHARS.charAt(index));
        }
        String Str = stringBuilder.toString();
        return Str;

    }

    /*
    Test add mood Anger
     */
    public void test1_AddMood() {
        actionLogin();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Anger");

        solo.clickOnView((EditText) solo.getView(R.id.view_date));
        solo.clickOnText("OK");
        solo.clickOnText("OK");

        solo.enterText((EditText) solo.getView(R.id.view_reason), "test reason");

        solo.clickOnText("Save Mood");

        solo.assertCurrentActivity("Wrong Activity", MoodBase.class);

        assertEquals(solo.searchText("Anger"), true);

        actionLogOut();
    }

    /*
    Test filter mood
    Add a Shame mood
    Filter it out and check if shame is still visible
     */
    public void test2_FilterByMood () {
        actionLogin();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Shame");
        solo.clickOnView((EditText) solo.getView(R.id.view_date));
        solo.clickOnText("OK");
        solo.clickOnText("OK");
        solo.enterText((EditText) solo.getView(R.id.view_reason), "test reason");
        solo.clickOnText("Save Mood");

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.filterButton));
        solo.clickOnText("Anger");
        solo.clickOnText("OK");
        solo.clickOnText("No");
        solo.clickOnText("No");
        assertFalse(solo.searchText("Shame"));

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.refreshButton));
        solo.clickLongOnText("Shame");
        solo.clickOnText("Delete");

        actionLogOut();
    }

    /*
    Add a mood with random reason generated
    Filter it and chekc if it is visible
     */
    public void test2_1_FilterByText () {
        actionLogin();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Shame");
        solo.clickOnView((EditText) solo.getView(R.id.view_date));
        solo.clickOnText("OK");
        solo.clickOnText("OK");
        String reason = getRDString();
        solo.enterText((EditText) solo.getView(R.id.view_reason), reason);
        solo.clickOnText("Save Mood");

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.filterButton));
        solo.clickOnText("OK");
        char[] ch_array = reason.toCharArray();
        for(int i=0;i<ch_array.length;i++)
        {
            solo.sendKey( android_keycode(ch_array[i]) );
        }
        solo.clickOnText("Yes");
        solo.clickOnText("No");
        assertTrue(solo.searchText("Shame"));

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.refreshButton));
        solo.clickLongOnText("Shame");
        solo.clickOnText("Delete");

        actionLogOut();
    }

    /*
    Filter by date
    A mood is already added dated back few months
    Filtering by date would hide the mood
     */
    public void test2_2_FilterByDate () {
        actionLogin();

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.fab));
        solo.assertCurrentActivity("Wrong Activities", ViewMood.class);
        Spinner emoij = (Spinner) solo.getView(R.id.spinner_emotion);
        solo.clickOnView(emoij);
        solo.clickOnText("Shame");
        solo.clearEditText((EditText) solo.getView(R.id.view_date));
        solo.clickOnText("OK");
        solo.clickOnText("OK");
        solo.clickOnText("Save Mood");

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.filterButton));
        solo.clickOnText("OK");
        solo.clickOnText("No");
        solo.clickOnText("Yes");
        assertFalse(solo.searchText("Surprise"));
        assertTrue(solo.searchText("Shame"));

        solo.clickOnView((FloatingActionButton) solo.getView(R.id.refreshButton));
        solo.clickLongOnText("Shame");
        solo.clickOnText("Delete");

        actionLogOut();
    }

    /*
    Edit mood added in test1 to a Sad Mood
     */
    public void test3_EditMood() {
        actionLogin();
        solo.clickLongInList(0);

        solo.clickOnText("View/Edit");

        solo.assertCurrentActivity("Wrong Activity", ViewMood.class);

        solo.clickOnView((Spinner) solo.getView(R.id.spinner_emotion));

        solo.clickOnText("Sad");

        solo.clickOnText("Save Mood");

        solo.assertCurrentActivity("Wrong Activity", MoodBase.class);

        assertEquals(solo.searchText("Anger"), false);
        assertEquals(solo.searchText("Sad"), true);

        actionLogOut();
    }

    /*
    Delete Mood added in test1
     */
    public void test4_DeleteMood() {
        actionLogin();
        solo.clickLongInList(0);

        solo.clickOnText("Delete");

        assertEquals(solo.searchText("Sad"), false);
        actionLogOut();
    }

    /*
    Test adding comment to a mood of people following
    Add a comment of random generated string
    Check if it's in the comments
     */
    public void test5_AddComment() {
        actionLogin();
        solo.clickOnText("Following");

        solo.clickLongOnText("haha");

        solo.clickOnText("View");

        solo.clickOnText("Add Comment");

        String comment = getRDString();

        char[] ch_array = comment.toCharArray();
        for(int i=0;i<ch_array.length;i++)
        {
            solo.sendKey( android_keycode(ch_array[i]) );
        }

        solo.clickOnText("OK");

        solo.clickOnText("View Comments");

        assertTrue(solo.searchText(comment));
        solo.goBack();
        solo.goBack();
        actionLogOut();
    }

    /*
    Decode string to ascii to input in pop up textEdit
     */
    public int android_keycode(char ch) {
        int keycode = ch;//String.valueOf(ch).codePointAt(0);
        Log.v("T","in fun : "+ch+" : "+keycode + "");

        if(keycode>=97 && keycode <=122)
        {
            Log.v("T","atoz : "+ch+" : "+keycode + " : " + (keycode-68));
            return keycode-68;
        }
        else if(keycode>=65 && keycode <=90)
        {
            Log.v("T","atoz : "+ch+" : "+keycode + " : " + (keycode-36));
            return keycode-36;
        }
        else if(keycode>=48 && keycode <=57)
        {
            Log.v("T","0to9"+ch+" : "+keycode + " : " + (keycode-41));
            return keycode-41;
        }
        else if(keycode==64)
        {
            Log.v("T","@"+ch+" : "+keycode + " : " + "77");
            return KeyEvent.KEYCODE_AT;
        }
        else if(ch=='.')
        {
            Log.v("T","DOT "+ch+" : "+keycode + " : " + "158");
            return KeyEvent.KEYCODE_PERIOD;
        }
        else if(ch==',')
        {
            Log.v("T","comma "+ch+" : "+keycode + " : " + "55");
            return KeyEvent.KEYCODE_COMMA;
        }
        return 62;
    }
}