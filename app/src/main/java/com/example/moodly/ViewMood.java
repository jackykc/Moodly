package com.example.moodly;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class ViewMood extends AppCompatActivity {

    private Mood mood;
    private EditText editReasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);
        Button saveButton = (Button) findViewById(R.id.save_button);
        EditText editDate = (EditText) findViewById(R.id.edit_date);
        editReasonText = (EditText) findViewById(R.id.edit_reason_text);
        // Taken from http://stackoverflow.com/questions/13408419/how-do-i-tell-if-intent-extras-exist-in-android 3/8/2017 22:08
        //https://www.tutorialspoint.com/android/android_spinner_control.htm
        final Spinner emotionSpinner = (Spinner) findViewById(R.id.spinner_emotion);
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

        final Spinner socialSituationSpinner = (Spinner) findViewById(R.id.spinner_SS);
        ArrayList<String> ssList = new ArrayList<>();
        ssList.add("Choose a social situation");
        ssList.add("Alone");
        ssList.add("With one other person");
        ssList.add("With two to several people");
        ssList.add("With a crowd");

        ArrayAdapter<String> ssAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,ssList);
        ssAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(ssAdapter);
        Intent intent = getIntent();
        boolean newMood = intent.hasExtra("PLACEHOLDER_MOOD");

        if (newMood == false){
            boolean oldMood = intent.hasExtra("selected_mood");
            if (oldMood == true){
                mood = getIntent().getParcelableExtra("selected_mood");
            }
        }
        else{
            mood = getIntent().getParcelableExtra("PLACEHOLDER_MOOD");
        }
        editDate.setText(mood.getDate().toString());
        editReasonText.setText(mood.getReasonText());

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // might be best to do a calendar for the date?
                // Date date = editDate.getText().toString();
                String reasonText = editReasonText.getText().toString();
                Emotion emotionEnum = Emotion.values()[emotionSpinner.getSelectedItemPosition()];
                SocialSituation socialEnum = SocialSituation.values()[socialSituationSpinner.getSelectedItemPosition()];
//                if (emotionEnum == 0)
//                    ((TextView)emotionSpinner.getSelectedView()).setError("Mood required");
//                else {
                    mood.setReasonText(reasonText);
                    mood.setEmotion(emotionEnum);
                    mood.setSocialSituation(socialEnum);

                    Intent output = new Intent(ViewMood.this, ViewMoodList.class);
                    output.putExtra("VIEWMOOD_MOOD", mood);
                    setResult(RESULT_OK, output);
                    finish();
//                }
            }

        });

    }

}

