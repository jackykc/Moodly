package com.example.moodly.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.moodly.Controllers.MoodController;
import com.example.moodly.Models.Emotion;
import com.example.moodly.Models.Mood;
import com.example.moodly.Models.SocialSituation;
import com.example.moodly.R;

import java.util.ArrayList;

import static com.example.moodly.Models.Emotion.NONE;

/**
 * ViewMood allows the user to view their
 * selected mood and it's relevant details
 */
public class ViewMood extends AppCompatActivity {

    private Mood mood;
    private EditText editReasonText;
    private EditText editDate;

    private Spinner emotionSpinner;
    private Spinner socialSituationSpinner;
    private Button saveButton;

    private int position = -1;

    /**
     * Gets the mood event and position,
     * sets view and listeners to it.
     * @param savedInstanceState
     * @see #setViews()
     * @see #setListeners()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);

        mood = MoodController.getInstance().getMood();
        position = getIntent().getIntExtra("MOOD_POSITION", -1);

        setViews();

        setListeners();

    }

    /**
     * Sets the spinners for emotional state and
     * social situation options.
     */
    protected  void setSpinners() {

        /*do we want to hardcode the following?
        * there probably is a better way to do this using the string resource file*/
        ArrayList<String> emotionList = new ArrayList<>();
        emotionList.add("Choose an emotion");
        emotionList.add("Anger");
        emotionList.add("Confusion");
        emotionList.add("Disgust");
        emotionList.add("Fear");
        emotionList.add("Happiness");
        emotionList.add("Sadness");
        emotionList.add("Shame");
        emotionList.add("Surprise");

        ArrayAdapter<String> emotionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emotionList);
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(emotionAdapter);


        // get views
        ArrayList<String> ssList = new ArrayList<>();
        ssList.add("Choose a social situation");
        ssList.add("Alone");
        ssList.add("With one other person");
        ssList.add("With two to several people");
        ssList.add("With a crowd");

        ArrayAdapter<String> ssAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,ssList);
        ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(ssAdapter);
    }


    protected void setViews() {
        saveButton = (Button) findViewById(R.id.save_button);

        editDate = (EditText) findViewById(R.id.view_date);
        editReasonText = (EditText) findViewById(R.id.view_reason);

        editDate.setText(mood.getDate().toString(), TextView.BufferType.EDITABLE);
        editReasonText.setText(mood.getReasonText(), TextView.BufferType.EDITABLE);

        // Taken from http://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android 3/8/2017 22:08
        //https://www.tutorialspoint.com/android/android_spinner_control.htm
        emotionSpinner = (Spinner) findViewById(R.id.spinner_emotion);
        socialSituationSpinner = (Spinner) findViewById(R.id.spinner_SS);

        setSpinners();

        emotionSpinner.setSelection(mood.getEmotion());
        socialSituationSpinner.setSelection(mood.getSocialSituation());

        if (position == -2)
        {
            editDate.setEnabled(false);
            editDate.setCursorVisible(false);
            editDate.setKeyListener(null);

            editReasonText.setEnabled(false);
            editReasonText.setCursorVisible(false);
            editReasonText.setKeyListener(null);
        }
    }
    // inherit from a view only class?
    //@Override

    /**
     * Set save button listener to validate
     * and save editted mood event from user.
     *
     */
    protected void setListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // might be best to do a calendar for the date?
                //Date date = editDate.getText().toString();
                //mood.setDate(editDate.getText().toD);
                // do i need to set it? is it a good idea?
                // adds mood to controller/elastic search server

                Emotion emotionEnumCheck = Emotion.values()[emotionSpinner.getSelectedItemPosition()];

//                Emotion check = NONE;
                if (emotionEnumCheck == NONE) {
                    // http://stackoverflow.com/questions/28235689/how-can-an-error-message-be-set-for-the-spinner-in-android 3/8/2017
                    TextView errorText = (TextView) emotionSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Emotion required");
                }
                else {

                    String reasonText = editReasonText.getText().toString();
                    int emotionEnum = emotionSpinner.getSelectedItemPosition();
                    int socialEnum = socialSituationSpinner.getSelectedItemPosition();

                    mood.setReasonText(reasonText);
                    mood.setEmotion(emotionEnum);
                    mood.setSocialSituation(socialEnum);

                    // needed to set the mood?
                    MoodController.getInstance().setMood(mood);
                    MoodController.getInstance().addMood(position, mood);
                    //MoodController.getInstance().editMood();

                    Intent output = new Intent(ViewMood.this, ViewMoodList.class);

                    setResult(RESULT_OK, output);
                    finish();
                }
            }

        });


    }

}

