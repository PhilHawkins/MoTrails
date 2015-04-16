package edu.csus.teamname.motrails;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RecordTrailActivity extends Activity implements OnMapReadyCallback {
    LocationListener locationListener;
    LocationManager lm;
    GoogleMap map;
    ArrayList<LatLng> routePoints;
    Polyline curLine;
    PolylineOptions track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_trail);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();

        try {
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new MoTrailsLocationListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_record_trail, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
    }

    public void startRecording(View view){

        routePoints = new ArrayList<LatLng>();
        track = new PolylineOptions();
        int blue = android.graphics.Color.rgb(0, 55, 255);
        track.color( blue );

       ViewGroup layout = (ViewGroup) view.getParent();
        if(null!=layout){
            layout.removeView(view);
            findViewById(R.id.stopRecordingButton).setVisibility(View.VISIBLE);

        }



//        LocationRequest mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(10000);
//        mLocationRequest.setFastestInterval(5000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addWaypoint() {
        Log.d("addWaypoint", "add waypoint function called");
    }

    public void stopRecording(View view){
        lm.removeUpdates(locationListener);

        JSONObject featureCollection = new JSONObject();

        JSONArray features = new JSONArray();

        Log.d("stop", "stop");
        for(LatLng ll : routePoints){
            JSONObject feature = new JSONObject();
            JSONObject geometry = new JSONObject();
            JSONArray coordinates = new JSONArray();
            try {
                feature.put("type", "Feature");
                geometry.put("type", "Point");
                coordinates.put(ll.longitude);
                coordinates.put(ll.latitude);
                Log.d("coordinates", coordinates.toString());
                geometry.put("coordinates", coordinates);
                Log.d("geometry", geometry.toString());
                feature.put("geometry", geometry);
                Log.d("feature", feature.toString());
                features.put(feature);
                Log.d("features", features.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("exception", e.toString());
            }
        }
        try {
            featureCollection.put("featureCollection", featureCollection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("geojson", features.toString());
    }

    public void addTrailPoint(Location location){

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        LatLng latLng = new LatLng(lat, lng);
        track.add(latLng);
        Log.d("addPoint", latLng.toString());
        routePoints.add(latLng);
        Log.d("routePoints", routePoints.toString());
        if(curLine != null){
            curLine.remove();
        }
        curLine = map.addPolyline(track);
        int blue = android.graphics.Color.rgb(0, 55, 255);
        curLine.setColor(blue);
    }
}
