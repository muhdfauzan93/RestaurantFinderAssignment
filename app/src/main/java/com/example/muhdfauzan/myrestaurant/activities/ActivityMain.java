package com.example.muhdfauzan.myrestaurant.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.adapter.AdapterRestaurant;
import com.example.muhdfauzan.myrestaurant.config.DataManager;
import com.example.muhdfauzan.myrestaurant.model.ModelChart;
import com.example.muhdfauzan.myrestaurant.model.ModelRestaurant;
import com.example.muhdfauzan.myrestaurant.utils.DatabaseHandler;
import com.example.muhdfauzan.myrestaurant.utils.GPSTracker;
import com.example.muhdfauzan.myrestaurant.utils.HttpHandler;
import com.example.muhdfauzan.myrestaurant.utils.JSONParser;
import com.example.muhdfauzan.myrestaurant.utils.RecyclerTouchListener;
import com.example.muhdfauzan.myrestaurant.utils.SimpleDividerItemDecoration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    private static final String LOG_TAG = ActivityMain.class.getSimpleName();
    private ArrayList<ModelRestaurant> restaurantsArrayList = new ArrayList<ModelRestaurant>();
    private ImageButton btn_search;
    private EditText search_key;
    private String search;
    private AdapterRestaurant mAdapter;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    // GPSTracker class
    GPSTracker gps;
    private double longitude = 0, latitude = 0;

    private static final int REQUEST_CODE_PERMISSION = 1;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(R.string.app_name);

        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        if(Build.VERSION.SDK_INT>= 23) {

            if (checkSelfPermission(mPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityMain.this,
                        new String[]{mPermission,
                        },
                        REQUEST_CODE_PERMISSION);
                return;
            }

            else
            {
                Intent intent = new Intent(this, ActivityGPS.class);
                startActivityForResult(intent,1);

                //gps = new GPSTracker(getApplicationContext());
                //latitude = gps.getLatitude();
                //longitude = gps.getLongitude();

            }
        }

        initialize();
        new getRestaurants().execute();
        // Making a request to url and getting response

        SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        //editor.putString("name", n);
        //editor.putString("quantity", ph);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                restaurantsArrayList.clear();
                new getRestaurants().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_search = (ImageButton)findViewById(R.id.btnSearch);
        search_key = (EditText)findViewById(R.id.et_search);

        DatabaseHandler db = new DatabaseHandler(this);
        db.deleteAll();

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addMenu(new ModelChart("test", "QEQWe", "qwqe", "www", "ww", "ww"));
        // Reading all menu
        Log.d("Reading: ", "Reading all contacts..");
        List<ModelChart> chart = db.getAllChart();

        for (ModelChart cn : chart) {
            String log = "Id: " + cn.getId() + " ,Name: " + cn.getMenuName() + " ,Restaurant id: " + cn.getRestId() + " ,Type: " + cn.getMenuType() +
                    " ,Price: " + cn.getMenuPrice() + " ,Details: " + cn.getMenuDetail();
            // Writing Contacts to log
            Log.d("Name: ", log);
        }

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = search_key.getText().toString();
                restaurantsArrayList.clear();
                new searchRestaurant().execute();
            }
        });



    }

    public void initialize() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new AdapterRestaurant(this, restaurantsArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // when chat is clicked, launch full chat thread activity
                ModelRestaurant restaurant = restaurantsArrayList.get(position);
                Intent i = new Intent(ActivityMain.this, ActivityMenuList.class);
                i.putExtra("r_id", restaurant.getR_id());
                startActivity(i);


            }

            @Override
            public void onLongClick(View view, int position) {
                ModelRestaurant restaurant = restaurantsArrayList.get(position);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+restaurant.getLatitude()+","+restaurant.getLongitude()+""));
                startActivity(intent);

            }
        }));

    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class getRestaurants extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ActivityMain.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(DataManager.RESTAURANT_URL);


            if (jsonStr != null) {
                int success;
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    List<NameValuePair> params = new ArrayList<NameValuePair>();

                    JSONObject json = jsonParser.makeHttpRequest(DataManager.RESTAURANT_URL,
                            "GET", params);

                    Log.d(LOG_TAG,"Json Get Single Chat: " + json);

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully received product details
                        JSONArray msgObj = json.getJSONArray("restaurants"); // JSON Array
                        for (int i = 0; i <msgObj.length() ; i++) {
                            JSONObject message = msgObj.getJSONObject(i);

                            String id = message.getString("id");
                            String name = message.getString("name");
                            String address = message.getString("address");
                            String lat = message.getString("lat");
                            String longt = message.getString("longt");
                            String open_hour = message.getString("open_hour");
                            String image = message.getString("image");
                            //String count = message.getString("message");
                            // message with this pid found
                            ModelRestaurant addRestaurant= new ModelRestaurant(id,name,address,lat,longt,open_hour, image);
                            restaurantsArrayList.add(addRestaurant);


                        }

                    }else{
                        // product with pid not found
                        // Toast.makeText(ActivityInbox.this, "Message not found", Toast.LENGTH_SHORT).show();
                    }


                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            mAdapter.notifyDataSetChanged();
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Bundle extras = data.getExtras();
            longitude = extras.getDouble("Longitude");
            latitude= extras.getDouble("Latitude");
        }
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class searchRestaurant extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ActivityMain.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(DataManager.RESTAURANT_SEARCH_URL);


            if (jsonStr != null) {
                int success;
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("search", search));

                    JSONObject json = jsonParser.makeHttpRequest(DataManager.RESTAURANT_SEARCH_URL,
                            "GET", params);

                    Log.d("Search restaurant",json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully received product details
                        JSONArray msgObj = json.getJSONArray("restaurants"); // JSON Array
                        for (int i = 0; i <msgObj.length() ; i++) {
                            JSONObject message = msgObj.getJSONObject(i);

                            String id = message.getString("id");
                            String name = message.getString("name");
                            String address = message.getString("address");
                            String lat = message.getString("lat");
                            String longt = message.getString("longt");
                            String open_hour = message.getString("open_hour");
                            String image = message.getString("image");
                            //String count = message.getString("message");
                            // message with this pid found
                            ModelRestaurant addRestaurant= new ModelRestaurant(id,name,address,lat,longt,open_hour, image);
                            restaurantsArrayList.add(addRestaurant);


                        }

                    }else{
                        // product with pid not found
                        // Toast.makeText(ActivityInbox.this, "Message not found", Toast.LENGTH_SHORT).show();
                    }


                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            mAdapter.notifyDataSetChanged();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.chart:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}



