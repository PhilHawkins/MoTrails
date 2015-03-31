package edu.csus.teamname.motrails;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by philt_000 on 3/30/2015.
 */
public class MoTrailsLocationListener implements LocationListener {
    private RecordTrailActivity currentActivity;
    final String _logTag = "Monitor Location";

    public MoTrailsLocationListener(RecordTrailActivity activity){
        currentActivity = activity;
    }

    @Override
    public void onLocationChanged(Location location) {
//        String provider = location.getProvider();
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
//        float accuracy = location.getAccuracy();
//        long time = location.getTime();

        currentActivity.addTrailPoint(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(_logTag, "Monitor Location = Provider Enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {

        Log.d(_logTag, "Monitor Location = Provider Disabled");
    }
}
