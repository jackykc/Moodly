package com.example.moodly.Models;

import java.util.ArrayList;

import io.searchbox.annotations.JestId;

/**
 * Created by mliew on 2017-02-25.
 */

public class User {

    private String name;
    private ArrayList<String> follower;
    private ArrayList<String> following;
    private ArrayList<String> requests;

    @JestId
    private String id;

    public User() {
        follower = new ArrayList<>();
        following = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public User(String myName) {
        name = myName;
        follower = new ArrayList<>();
        following = new ArrayList<>();
        requests = new ArrayList<>();
    }

    public ArrayList<Mood> moodList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFollowers() { return follower; }

    public ArrayList<String> getFollowing() { return following; }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public void addFollower(User user){
        //Bug-proof
        if (!follower.contains(user.getName())){
            //Add my follower
            follower.add(user.getName());

            //Add the other user following
            user.addFollowing(this);
        }
    }

    public void addFollowing(User user){
        //Bug-proof
        if (!following.contains(user.getName())){
            //Add my following
            following.add(user.getName());

            //Add the other user follower
            user.addFollower(this);
        }
    }

    public void removeFollower(User user) {
        if (follower.contains(user.getName())){
            //Remove my follower
            int index = follower.indexOf(user.getName());
            follower.remove(index);

            //Remove following of the other user
            user.removeFollowing(this);
        }
    }

    public void removeFollowing(User user) {
        if (following.contains(user.getName())){
            //Remove my following
            int index = following.indexOf(user.getName());
            following.remove(index);

            //Remove follower of the other user
            user.removeFollower(this);
        }
    }

}
