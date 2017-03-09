package com.example.moodly;

import android.content.Intent;
import android.graphics.Color;
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

import static com.example.moodly.Emotion.NONE;

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
        mood = MoodController.getInstance().getMood();


        // get views
        editDate = (EditText) findViewById(R.id.edit_date);
        editReasonText = (EditText) findViewById(R.id.edit_reason_text);

        // set views
        editDate.setText(mood.getDate().toString(), TextView.BufferType.EDITABLE);
        editReasonText.setText(mood.getReasonText(), TextView.BufferType.EDITABLE);

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
                mood = MoodController.getInstance().getMood();
                //http://stackoverflow.com/questions/11072576/set-selected-item-of-spinner-programmatically 3/8/2017
                //String oldEmotion = toStringEmotion(mood.getEmotion());
                //String oldSS = toStringSS(mood.getSocialSituation());
                String oldEmotion = Integer.toString(mood.getEmotion());
                String oldSS = Integer.toString(mood.getSocialSituation());

                emotionSpinner.setSelection(emotionList.indexOf(oldEmotion));
                socialSituationSpinner.setSelection(ssList.indexOf(oldSS));
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
                //Date date = editDate.getText().toString();
                //mood.setDate(editDate.getText().toD);
                // do i need to set it? is it a good idea?
                // adds mood to controller/elastic search server


                //Emotion emotionEnum = Emotion.values()[emotionSpinner.getSelectedItemPosition()];
                Emotion emotionEnumCheck = Emotion.values()[emotionSpinner.getSelectedItemPosition()];
                SocialSituation socialEnumCheck = SocialSituation.values()[socialSituationSpinner.getSelectedItemPosition()];

                Emotion check = NONE;
                if (emotionEnumCheck == check) {
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

                    MoodController.getInstance().setMood(mood);
                    MoodController.AddMoodTask addMoodTask = new MoodController.AddMoodTask();
                    addMoodTask.execute(mood);

                    Intent output = new Intent(ViewMood.this, ViewMoodList.class);

                    setResult(RESULT_OK, output);
                    finish();
                }
            }

        });

    }
    private String toStringEmotion(Emotion emotion) {
        switch (emotion) {
            case ANGER:
                return "Anger";
            case CONFUSION:
                return "Confusion";
            case DISGUST:
                return "Disgust";
            case FEAR:
                return "Fear";
            case HAPPINESS:
                return "Happiness";
            case SADNESS:
                return "Sadness";
            case SHAME:
                return "Shame";
            case SUPRISE:
                return "Surprise";
            default:
                return "None";
        }
    }

    private String toStringSS(SocialSituation socialSituation) {
        switch (socialSituation) {
            case ALONE:
                return "Alone";
            case ONE:
                return "With one other person";
            case SEVERAL:
                return "With two to several people";
            case CROWD:
                return "With a crowd";
            default:
                return "None";
        }
    }

}

