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


public class Map extends Fragment {

    private static View view;


    private static GoogleMap mMap;
    private static Double latitude, longitude;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.map_layout, container, false);
        setUpMapIfNeeded(); // For setting up the MapFragment
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    // Sets up the map
    public static void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) MainDrawer.fragmentManager.findFragmentById(R.id.map)).getMap();
            // Double checking the mMap variable
            if (mMap != null)
                initializeMap();
        }
    }
    private static void initializeMap() {
        // Move to current Location button
        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.8282, -98.5795), 3.0f));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (mMap != null)
            initializeMap();

        if (mMap == null) {
            mMap = ((MapFragment) MainDrawer.fragmentManager
                    .findFragmentById(R.id.map)).getMap();

            if (mMap != null)
                initializeMap();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            MainDrawer.fragmentManager.beginTransaction()
                      .remove(MainDrawer.fragmentManager
                      .findFragmentById(R.id.map)).commit();
            mMap = null;
        }
    }
}