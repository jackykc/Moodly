//package com.example.moodly;
//
//import java.util.ArrayList;
//
///**
// * Created by MinhNguyen on 06/03/2017.
// */
//
//public class MoodController {
//    private static final MoodController instance = new MoodController();
//    ArrayList<Mood> moodList;
//
//    private MoodController() {
//        moodList = new ArrayList<Mood>();
//    }
//
//    public static MoodController getInstance() {
//        return instance;
//    }
//
//    private void addMood(Mood m){
//        instance.moodList.add(m);
//    }
//
//    private void editMood (int position, Mood newMood) {
//        moodList.remove(position);
//        moodList.add(position, newMood);
//    }
//
//    private void deleteMood(int position) {
//        moodList.remove(position);
//    }
//
//    private String getLocation(int position) {
//        Mood m = moodList.get(position);
//        return m.getLocation();
//    }
//
//    private ArrayList<Mood> filer(){
//        return moodList;
//    }
//}
package com.example.moodly;

import java.util.ArrayList;

/**
 * Created by MinhNguyen on 06/03/2017.
 */


// https://www.youtube.com/watch?v=NZaXM67fxbs singleton design pattern Mar 06
public class MoodController {
    // this isn't safe from synchronization, does it need to be?
    // i don't know how to verify that, but i guess we will find out
    // soon
    private static MoodController instance = null;
    private ArrayList<Mood> moodList;
    private ArrayList<Mood> moodFollowList;
    private ArrayList<Mood> filteredList;

    private MoodController() {
        moodList = new ArrayList<Mood>();
        moodFollowList = new ArrayList<Mood>();
        filteredList = new ArrayList<Mood>();
    }

    public static MoodController getInstance() {

        if(instance == null) {
            instance = new MoodController();
        }

        return instance;
    }

    public void addMood(Mood m){
        instance.moodList.add(m);
    }

    public void editMood (int position, Mood newMood) {
        moodList.remove(position);
        moodList.add(position, newMood);
    }

    public void deleteMood(int position) {
        moodList.remove(position);
    }


    public String getLocation(int position) {
        Mood m = moodList.get(position);
        return m.getLocation();
    }



    public ArrayList<Mood> getFiltered() {
        this.filter();
        return this.filteredList;
    }
    // ??? make helper functions ?
    // only filters and sets the filtered list as filteredList
    // moodList should be the full list
    // filtering person, good luck
    // so, currently all this does is set filteredList to reference
    // each of its elemnts from moodList, this is not a deep copy
    private void filter(){
        filteredList = (ArrayList<Mood>) moodList.clone();
    }
}
