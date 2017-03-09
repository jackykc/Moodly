package com.example.moodly;

import java.util.Date;

import io.searchbox.annotations.JestId;

/**
 * Created by jkc1 on 2017-03-07.
 */

public class Mood1 {
    public Date date;
    public String owner;
    public String location;
    public String trigger;
    public String reasonText;
    public String image;
    public Integer emotion;
    public Integer socialSituation;

    @JestId
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The constructor for the mood class
     */
    public Mood1() {
        this.date = new Date();
        this.owner = "Placeholder";
        this.location = "";
        this.trigger = "";
        this.emotion = 0;
        this.socialSituation = 0;
        this.reasonText = "";
        this.image = null;
    }
}
