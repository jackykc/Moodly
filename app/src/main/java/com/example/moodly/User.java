package com.example.moodly;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class User {
    private String name;
    public ArrayList<Mood> moodList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
