package com.example.moodly;

import android.graphics.ImageFormat;

import java.util.Date;

/**
 * Created by mliew on 2017-02-25.
 */

public class Mood {
    private Date date;
    private String owner;
    private String location;
    private String trigger;
    private String emotion;
    private String socialSituation;
    private String reasonText;
    private ImageFormat image;
    // not sure about what format to use for the picture

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(String socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public ImageFormat getImage() {
        return image;
    }

    public void setImage(ImageFormat image) {
        this.image = image;
    }
}
