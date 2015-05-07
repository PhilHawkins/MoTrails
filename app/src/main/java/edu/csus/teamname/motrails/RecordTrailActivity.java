package edu.csus.teamname.motrails;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    private static final int WAYPOINT_REQUEST = 777;

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
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(RecordTrailActivity.this, R.string.please_turn_on_gps, Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            RecordTrailActivity.this.startActivity(myIntent);
        }
        routePoints = new ArrayList<LatLng>();
        track = new PolylineOptions();
        int blue = android.graphics.Color.rgb(0, 55, 255);
        track.color( blue );

       ViewGroup layout = (ViewGroup) view.getParent();
        if(null!=layout){
            layout.removeView(view);
            findViewById(R.id.stopRecordingButton).setVisibility(View.VISIBLE);
            findViewById(R.id.addWaypointButton).setVisibility(View.VISIBLE);
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

    public void addWaypoint(View view) {

        Log.d("addWaypoint", "add waypoint function called");

        Location loc = lm.getLastKnownLocation(lm.GPS_PROVIDER);
        Intent i = new Intent(this, AddWaypoint.class);
        i.putExtra("location", loc);
        startActivityForResult(i, WAYPOINT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == WAYPOINT_REQUEST && resultCode == RESULT_OK){
            //data.get
        }
    }

    public void stopRecording(View view){
        lm.removeUpdates(locationListener);

        JSONObject featureCollection = new JSONObject();

        JSONArray features = new JSONArray();

        JSONObject feature = new JSONObject();
        JSONObject geometry = new JSONObject();
        JSONArray coordinates = new JSONArray();

        try {
            feature.put("type", "Feature");
            geometry.put("type", "LineString");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for(LatLng ll : routePoints){
            try {
                JSONArray pointCoords = new JSONArray();
                pointCoords.put(ll.longitude);
                pointCoords.put(ll.latitude);
                coordinates.put(pointCoords);
            }
            catch (JSONException e){
                e.printStackTrace();

            }
        }
        try {
            geometry.put("coordinates", coordinates);
            feature.put("geometry", geometry);
            features.put(feature);
            featureCollection.put("featureCollection", featureCollection);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("geojson", featureCollection.toString());
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
