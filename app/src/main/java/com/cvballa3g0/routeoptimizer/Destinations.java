package com.cvballa3g0.routeoptimizer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Destinations extends Fragment {
    View view;
    DBAdapter MY_DB;
    static Context CONTEXT;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor spEditor;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_main_drawer, container, false);
        openDB();
        populateListView();
        CONTEXT = getActivity();
        sharedPreferences = CONTEXT.getSharedPreferences("RouteOptimizer", Context.MODE_PRIVATE);
        spEditor = sharedPreferences.edit();

        // start address edittext
        final AutoCompleteTextView startCompView = (AutoCompleteTextView) view.findViewById(R.id.startAddressAutoComplete);
        startCompView.setText(sharedPreferences.getString("startCompView", ""));
        startCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list));
        startCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String address = (String) adapterView.getItemAtPosition(position);
                spEditor.putString("startCompView", address);
                spEditor.commit();
            }
        });

        // end address edittext
        final AutoCompleteTextView endCompView = (AutoCompleteTextView) view.findViewById(R.id.endAddressAutoComplete);
        endCompView.setText(sharedPreferences.getString("endCompView", ""));
        endCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list));
        endCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long id) {
                String address = (String) adapterView.getItemAtPosition(position);
                spEditor.putString("endCompView",address);
                spEditor.commit();
            }
        });

        // add destination edittext
        final AutoCompleteTextView addCompView = (AutoCompleteTextView) view.findViewById(R.id.addAddressAutoComplete);
        addCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list));
        addCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int position, long id) {
                String str = (String) adapterView.getItemAtPosition(position);
                addAddress(str);
                addCompView.setText("");
            }
        });

        // clear all button
        Button addButton = (Button) view.findViewById(R.id.clearAllButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDB();
            }
        });

        // same end as start checkbox
        final CheckBox checkBoxSame = (CheckBox) view.findViewById(R.id.sameStartCheckBox);
            checkBoxSame.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBoxSame.isChecked()) {
                        endCompView.setEnabled(false);
                        endCompView.setText(startCompView.getText().toString());
                        spEditor.putString("endCompView",endCompView.getText().toString());
                        spEditor.commit();
                    } else {
                        endCompView.setEnabled(true);
                        endCompView.setText(sharedPreferences.getString("endCompView", ""));
                    }
                }
            });
        //destination listview
        final ListView destListView = (ListView) view.findViewById(R.id.destinationListView);
        destListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                Cursor cursor = MY_DB.getRow(id);
                String address = cursor.getString(DBAdapter.COL_ADDRESS);
                cursor.close();
                String url = ("https://www.google.com/maps/place/" + address).replaceAll(" ", "+");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        destListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int position, long arg3) {
                PopupMenu popupMenu = new PopupMenu(getActivity().getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.main_drawer, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        onContextItemSelected(item, position);
                        return false;
                    }
                });
                popupMenu.show();
                return false;
            }
        });

        destListView.smoothScrollBy(1,1); //fix to stop from viewing Issue when longclicking


        return view;
    }

    public boolean onContextItemSelected(MenuItem item, int position) {
        int id = item.getItemId();

        if (id == R.id.optimize_settings) {
            optimize();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getAddress(long id) {
        Cursor cursor = MY_DB.getRow(id);
        String address = cursor.getString(DBAdapter.COL_ADDRESS);
        cursor.close();
        return null;
    }

    public static void optimize() {
        Toast.makeText(CONTEXT,"Optimize",Toast.LENGTH_LONG).show();
    }

    private void populateListView(){
        Cursor cursor = MY_DB.getAllRows();

        String[] addresses = new String[]{DBAdapter.KEY_ADDRESS};
        int[] textViewID = new int[]{R.id.streetAddress};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),R.layout.destination_list, cursor,addresses,textViewID, 1);

        ListView list = (ListView) view.findViewById(R.id.destinationListView);
        list.setAdapter(cursorAdapter);
    }

    private void clearDB() {
        MY_DB.deleteAll();
        populateListView();
    }

    private void addAddress(String str) {
        long myID = MY_DB.insertRow(str);
        populateListView();
    }

    private void closeDB() {
        MY_DB.close();
    }

    private void openDB() {
        MY_DB = new DBAdapter(getActivity());
        MY_DB.open();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeDB();
    }

    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
    static String API_KEY = "AIzaSyBAedPwY87B__w9sUmzG2QEdKmhc5z7JSk";

    private static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(API_URL);
            sb.append("?key=" + API_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("Autocomplete", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("Autocomplete", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e("Autocomplete", "Cannot process JSON results", e);
        }

        return resultList;
    }

    private static class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }
}
