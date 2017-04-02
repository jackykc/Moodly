package com.example.moodly.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.moodly.Controllers.MoodController;
import com.example.moodly.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class NearbyMoodActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_mood);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearbymap);
        mapFragment.getMapAsync(this);
        Context context = getApplicationContext();
        CharSequence text = "onCreate part";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Context context = getApplicationContext();
        CharSequence text = "onMapReady part";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        mMap = map;
        int height = 120;
        int width = 120;

        Bundle bundle = getIntent().getParcelableExtra("mapBundle");
        boolean listType = getIntent().getBooleanExtra("list_type", true);

        Bitmap anger = BitmapFactory.decodeResource(getResources(), R.drawable.angry);
        anger = Bitmap.createScaledBitmap(anger, width, height, false);
        Bitmap confusion = BitmapFactory.decodeResource(getResources(), R.drawable.confused);
        confusion = Bitmap.createScaledBitmap(confusion, width, height, false);
        Bitmap disgust = BitmapFactory.decodeResource(getResources(), R.drawable.disgust);
        disgust = Bitmap.createScaledBitmap(disgust, width, height, false);
        Bitmap fear = BitmapFactory.decodeResource(getResources(), R.drawable.afraid);
        fear = Bitmap.createScaledBitmap(fear, width, height, false);
        Bitmap happiness = BitmapFactory.decodeResource(getResources(), R.drawable.happy);
        happiness = Bitmap.createScaledBitmap(happiness, width, height, false);
        Bitmap sadness = BitmapFactory.decodeResource(getResources(), R.drawable.sad);
        sadness = Bitmap.createScaledBitmap(sadness, width, height, false);
        Bitmap shame = BitmapFactory.decodeResource(getResources(), R.drawable.shame);
        shame = Bitmap.createScaledBitmap(shame, width, height, false);
        Bitmap suprise = BitmapFactory.decodeResource(getResources(), R.drawable.surprise);
        suprise = Bitmap.createScaledBitmap(suprise, width, height, false);


        ArrayList<Bitmap> myEmojis = new ArrayList<Bitmap>();
        myEmojis.add(anger);
        myEmojis.add(confusion);
        myEmojis.add(disgust);
        myEmojis.add(fear);
        myEmojis.add(happiness);
        myEmojis.add(sadness);
        myEmojis.add(shame);
        myEmojis.add(suprise);

        MoodController moodController = MoodController.getInstance();
        // true for history list
        ArrayList<LatLng> myLocations = moodController.getLocations(listType);
        ArrayList<Integer> myEmotions = moodController.getEmotions(listType);

        LatLng invalidLatLng = new LatLng(0,0);
        for (int i = 0; i < myLocations.size(); i++) {
            LatLng temp = myLocations.get(i);
            // if locations are valid
            if(! temp.equals(invalidLatLng)) {
                int tempEmotion = myEmotions.get(i);

                Bitmap tempEmoji = myEmojis.get(tempEmotion-1);
                mMap.addMarker(new MarkerOptions().position(temp).icon(BitmapDescriptorFactory.fromBitmap(tempEmoji)).title("title"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));

            }
        }

//
//        if (icon.equals(AFRAID_WORD)) {BitmapDrawable bitmapdraw;
//            bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.afraid);
//            Bitmap b=bitmapdraw.getBitmap();
//            smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        }
//
//
//

//
//        LatLng mood0 = new LatLng((i).getLatitude(), getLongitude());
//        mMap.addMarker(new MarkerOptions().position(mood).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("title"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mood));
//
//        LatLng mood1 = new LatLng((i).getLatitude(), getLongitude());
//        mMap.addMarker(new MarkerOptions().position(mood).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("title"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mood));
//
//        LatLng mood2 = new LatLng((i).getLatitude(), getLongitude());
//        mMap.addMarker(new MarkerOptions().position(mood).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("title"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mood));
//
//        /*
//        ArrayList<double> longtitude =
//
//        for (int i = 0; i < moodList.size(); i++) {
//            String icon = moodList.(i).getMood().getText();
//
//            // initialize the marker so it exists
//            int height = 120;
//            int width = 120;
//
//
//            LatLng mood = new LatLng((i).getLatitude(), getLongitude());
//            mMap.addMarker(new MarkerOptions().position(mood).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("title"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(mood));
//        }
//        */
    }
}



//if (icon.equals(AFRAID_WORD)) {
//BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.afraid);
//    Bitmap b=bitmapdraw.getBitmap();
//smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
//        }
//        LatLng mood = new LatLng(moodEventList.getMoodEvent(i).getLatitude(), moodEventList.getMoodEvent(i).getLongitude());
//        mMap.addMarker(new MarkerOptions().position(mood).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title(moodEventList.getMoodEvent(i).getMood().getText()));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(mood));
//        }