package edu.rasmussen.mapoverlay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Main activity for the app. Can be used to push updates to the map fragment.
 */
public class MainActivity extends Activity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Test Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Update the map fragment with start and end points connected by a polyline.
     * @param start string to use for start marker
     * @param end string to use for destination marker
     * @param directions object to get directions from
     */
    public void updateMap(String start, String end, Directions directions)
    {
        if (directions.directions.size() == 0)
            return;
        // very first and last latlng in array contains starting and ending point
        int size = directions.latLngs.size();
        LatLng startPoint = directions.latLngs.get(0);
        LatLng endPoint = directions.latLngs.get(size-1);
        // add start and end marker
        mMap.addMarker(new MarkerOptions().position(startPoint).title(start));
        mMap.addMarker(new MarkerOptions().position(endPoint).title(end));
        // add polyline using points from directions
        mMap.addPolyline(new PolylineOptions()
                .addAll(directions.latLngs)
                .color(Color.BLUE));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
    }
}
