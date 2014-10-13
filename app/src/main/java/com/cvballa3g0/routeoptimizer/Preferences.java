package com.cvballa3g0.routeoptimizer;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Anthony on 10/13/2014.
 */
public class Preferences extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.preferences_layout);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
    }
}
