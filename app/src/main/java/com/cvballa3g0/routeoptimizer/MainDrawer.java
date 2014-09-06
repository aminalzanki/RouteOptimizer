package com.cvballa3g0.routeoptimizer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainDrawer extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static FragmentManager fragmentManager;
    static String API_KEY = "AIzaSyARnvog5wasFol4h4XtfUbfRmcPm3BhW5k";
    public ArrayAdapter<String> autoCompleteAdapter;
    public AutoCompleteTextView textView;
    int lastFragment = -1;



    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp( R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        fragmentManager = getFragmentManager();

/*
        autoCompleteAdapter = new ArrayAdapter<String>(this, R.layout.autocomplete_list);
        textView = (AutoCompleteTextView) findViewById(R.id.addAddressAutoComplete);
        autoCompleteAdapter.setNotifyOnChange(true);
        textView.setAdapter(autoCompleteAdapter);
        textView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 1) {
                    autoCompleteAdapter.clear();
                    AutoCompleteResults task = new AutoCompleteResults();
                    //now pass the argument in the textview to the task
                    task.execute(textView.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
*/
    }
        class AutoCompleteResults extends AsyncTask<String, Void, ArrayList<String>>  {

            @Override
            protected ArrayList<String> doInBackground(String... args) {
                ArrayList<String> resultArray = new ArrayList<String>();

                try {
                    URL googlePlaces = new URL(
                            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+ URLEncoder.encode(args[0].toString(), "UTF-8") +"&types=geocode&language=en&sensor=true&key=" + API_KEY);
                    URLConnection urlConnection = googlePlaces.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String data;
                    StringBuffer sb = new StringBuffer();

                    while ((data = in.readLine()) != null) {
                        sb.append(data); // gets the json data to string
                    }

                    JSONObject predictions = new JSONObject(sb.toString());

                    JSONArray jsonArray = new JSONArray(predictions.getString("predictions"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                        //add each entry to our array
                        resultArray.add(jsonObj.getString("description"));
                    }
                } catch (IOException e){
                    Log.e("GetAutoComplete", "IOException", e);
                } catch (JSONException e) {
                    Log.e("GetAutoComplete", "JSON Exception", e);
                }

                return resultArray;

            }

            @Override
            protected void onPostExecute(ArrayList<String> result) {
                Log.d("YourApp", "onPostExecute : " + result.size());
                //update the adapter
                autoCompleteAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.autocomplete_list);
                autoCompleteAdapter.setNotifyOnChange(true);
                //attach the adapter to textview
                textView.setAdapter(autoCompleteAdapter);

                for (String string : result) {
                    autoCompleteAdapter.add(string);
                    autoCompleteAdapter.notifyDataSetChanged();

                }
            }
        }






    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment myFragment = null;
        boolean switchFragment = false;
        switch (position+1) {
            case 1:
                mTitle = getString(R.string.title_section1);
                if (lastFragment != 1) {
                    myFragment = PlaceholderFragment.newInstance(position + 1);
                    lastFragment = 1;
                    switchFragment = true;
                }
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                if (lastFragment != 2) {
                    myFragment = new Map();
                    lastFragment = 2;
                    switchFragment = true;
                }
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                if (lastFragment != 3) {
                    myFragment = new Directions();
                    lastFragment = 3;
                    switchFragment = true;
                }
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                if (lastFragment != 4) {
                    Intent intent = new Intent(this, Preferences.class);
                    lastFragment = 4;
                    startActivity(intent);
                }
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                if (lastFragment != 5) {
                    myFragment = PlaceholderFragment.newInstance(position + 1);
                    lastFragment = 5;
                    switchFragment = true;
                }
                break;
        }
        if (switchFragment) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, myFragment).commit();
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.optimize_settings){
            //optimize();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_drawer, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainDrawer) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
