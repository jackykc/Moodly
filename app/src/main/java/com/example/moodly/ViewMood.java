package com.example.moodly;

import android.content.Intent;
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
    private EditText editDate, editReasonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood);

        Button saveButton = (Button) findViewById(R.id.save_button);

        //https://www.tutorialspoint.com/android/android_spinner_control.htm
        final Spinner emotionSpinner = (Spinner) findViewById(R.id.spinner_emotion);
        /*do we want to hardcode the following?
        * there probably is a better way to do this using the string resource file*/
        ArrayList<String> emotionList = new ArrayList<String>();
        emotionList.add("None");
        emotionList.add("Anger");
        emotionList.add("Confusion");
        emotionList.add("Disgust");
        emotionList.add("Fear");
        emotionList.add("Happiness");
        emotionList.add("Sadness");
        emotionList.add("Shame");
        emotionList.add("Suprise");

        ArrayAdapter<String> emotionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, emotionList);
        emotionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(emotionAdapter);



        //mood = getIntent().getParcelableExtra("PLACEHOLDER_MOOD");
        mood = MoodController.getInstance().getMood();


        // get views
        editDate = (EditText) findViewById(R.id.edit_date);
        editReasonText = (EditText) findViewById(R.id.edit_reason_text);

        // set views
        editDate.setText(mood.getDate().toString(), TextView.BufferType.EDITABLE);
        editReasonText.setText(mood.getReasonText(), TextView.BufferType.EDITABLE);

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // might be best to do a calendar for the date?
                //Date date = editDate.getText().toString();
                String reasonText = editReasonText.getText().toString();
                int emotionEnum = emotionSpinner.getSelectedItemPosition();

                //mood.setDate(editDate.getText().toD);
                mood.setReasonText(reasonText);
                mood.setEmotion(emotionEnum);
                // do i need to set it? is it a good idea?
                MoodController.getInstance().setMood(mood);
                MoodController.AddMoodTask addMoodTask = new MoodController.AddMoodTask();
                addMoodTask.execute(mood);


                Intent output = new Intent();

                //output.putExtra("VIEWMOOD_MOOD", mood);
                setResult(RESULT_OK, output);

                finish();
            }

        });

    }

}

