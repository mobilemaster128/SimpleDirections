package edu.rasmussen.mapoverlay;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Madison Liddell on 12/9/2015.
 * Simple lass created to use for saving directions. Contains two arraylists, one holding step by
 * step directions and the other holding the lat and long measurements for traveling between
 * the directional steps.
 */
public class Directions {
    public ArrayList<String> directions;
    public ArrayList<LatLng> latLngs;

    public Directions() {
    }

    public Directions(ArrayList<LatLng> latLngs, ArrayList<String> directions) {
        this.latLngs = latLngs;
        this.directions = directions;
    }
}
