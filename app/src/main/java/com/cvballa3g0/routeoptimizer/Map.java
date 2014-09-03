package com.cvballa3g0.routeoptimizer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class Map  extends Fragment {

    private static View view;

    private static GoogleMap mMap;
    private static Double latitude, longitude;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.map_layout, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 26.78;
        longitude = 72.56;

        setUpMapIfNeeded(); // For setting up the MapFragment
        return view;
    }

    /***** Sets up the map if it is possible to do so *****/
    public static void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) MainDrawer.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }
    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        // For zooming automatically to the Dropped PIN Location
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.8282, -98.5795), 6.0f));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) MainDrawer.fragmentManager
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            MainDrawer.fragmentManager.beginTransaction()
                    .remove(MainDrawer.fragmentManager.findFragmentById(R.id.map)).commit();
            mMap = null;
        }
    }
}