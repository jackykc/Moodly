package com.example.moodly;

import java.util.ArrayList;

/**
 * Created by mliew on 2017-02-25.
 */

public class User {
    private String name;
    private ArrayList<User> follower;
    private ArrayList<User> following;

    public User() {
        follower = new ArrayList<>();
        following = new ArrayList<>();
    }

    public ArrayList<Mood> moodList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getFollower() { return follower; }

    public ArrayList<User> getFollowing() { return following; }

    public void addFollower(User user){
        //Bug-proof
        if (!follower.contains(user)){
            //Add my follower
            follower.add(user);

            //Add the other user following
            user.addFollowing(this);
        }
    }

    public void addFollowing(User user){
        //Bug-proof
        if (!following.contains(user)){
            //Add my following
            following.add(user);

            //Add the other user follower
            user.addFollower(this);
        }
    }

    public void removeFollower(User user) {
        if (follower.contains(user)){
            //Remove my follower
            int index = follower.indexOf(user);
            follower.remove(index);

            //Remove following of the other user
            user.removeFollowing(this);
        }
    }

    public void removeFollowing(User user) {
        if (following.contains(user)){
            //Remove my following
            int index = following.indexOf(user);
            following.remove(index);

            //Remove follower of the other user
            user.removeFollower(this);
        }
    }

}
