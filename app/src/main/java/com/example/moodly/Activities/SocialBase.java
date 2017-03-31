package com.example.moodly.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.moodly.R;

/**
 * Created by Victor on 2017-03-07.
 * Base class that loads fragments associated with Social Functions
 *
 * @see SocialFollowingList
 * @see SocialRequestList
 * @see SocialUserSearch
 */

public class SocialBase extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_mood_list);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        // checks periodically
        handler = new Handler();
        synchronizeNetwork.run();

    }

    private int repeatInterval = 5000;
    private Handler handler;

    Runnable synchronizeNetwork = new Runnable() {
        @Override
        public void run() {
            try {
                if(! networkAvailable()) {
                    Toast.makeText(SocialBase.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                handler.postDelayed(synchronizeNetwork, repeatInterval);
            }
        }
    };



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_social) {
            Intent intent = new Intent(this, SocialBase.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * This medthod takes a position for the selected tabs and returns the tab
         * @param position the position of the selected tab (For history or following)
         */
        @Override
        public Fragment getItem(int position) {
            // return current tab
            SocialUserSearch tab1 = new SocialUserSearch();
            if (networkAvailable()) {
                switch (position) {
                    case 0:
                        return tab1;
                    case 1:
                        SocialFollowerList tab2 = new SocialFollowerList();
                        return tab2;
                    case 2:
                        SocialFollowingList tab3 = new SocialFollowingList();
                        return tab3;
                    case 3:
                        SocialRequestList tab4 = new SocialRequestList();
                        return tab4;
                    default:
                        return null;
                }
            }
            else {
                returnBack();
                return tab1;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        // set the titles of each tab
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Find People";
                case 1:
                    return "Followers";
                case 2:
                    return "Following";
                case 3:
                    return "Follow Requests";
            }
            return null;
        }
    }

    private boolean networkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void returnBack(){
        Toast.makeText(this,"Cannot use social functionality when offline!",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SocialBase.this,ViewMoodList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
