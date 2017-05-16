package com.example.muhdfauzan.myrestaurant.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.adapter.AdapterMenu;
import com.example.muhdfauzan.myrestaurant.config.DataManager;
import com.example.muhdfauzan.myrestaurant.model.ModelMenu;
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

public class ActivityMenuList extends AppCompatActivity{

    private static final String LOG_TAG = ActivityMenuList.class.getSimpleName();
    private ArrayList<ModelMenu> menuArrayList = new ArrayList<ModelMenu>();
    ;
    private AdapterMenu mAdapter;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private String r_id;
    private int total = 0;
    private TextView tv_total_price;
    private EditText search_key;
    private String search;
    private ImageButton btn_search;
    private Button btn_reserve, btn_chart, btn_custom_search;
    private float totalPrice = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("Menu");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        tv_total_price = (TextView)findViewById(R.id.tv_total_price);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                r_id = null;
            } else {
                r_id = extras.getString("r_id");
            }
        } else {
            r_id = (String) savedInstanceState.getSerializable("r_id");
        }

        initialize();
        new getMenu().execute();
        // Making a request to url and getting response

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                menuArrayList.clear();
                new getMenu().execute();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_search = (ImageButton)findViewById(R.id.btnSearch);
        search_key = (EditText)findViewById(R.id.et_search);
        btn_reserve = (Button)findViewById(R.id.btn_reserve);
        btn_chart = (Button)findViewById(R.id.btn_chart);
        btn_custom_search = (Button)findViewById(R.id.btn_adv_search);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = search_key.getText().toString();
                menuArrayList.clear();
                new searchMenu().execute();
            }
        });

        btn_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);
                int total = sharedpreferences.getInt("total", 0);

                if(total > 0) {
                    Intent i = new Intent(ActivityMenuList.this, ActivityReservation.class);
                    i.putExtra("r_id", r_id);
                    startActivity(i);
                }
                else
                    Toast.makeText(ActivityMenuList.this, "Cart is empty", Toast.LENGTH_SHORT).show();

            }
        });

        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMenuList.this, ActivityCart.class);
                startActivity(i);
            }
        });

        btn_custom_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityMenuList.this, ActivityAdvancedSearch.class);
                startActivity(i);
            }
        });


    }

    public void initialize() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new AdapterMenu(this, menuArrayList);
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
                ModelMenu menu = menuArrayList.get(position);
                SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                int total = sharedpreferences.getInt("total", 0);
                if (total != 0) {
                    editor.putInt("total", total + 1);
                    editor.putString("name_" + total, menu.getMenuName());
                    editor.putFloat("price_" + total, Float.parseFloat(menu.getMenuPrice()));
                    editor.commit();

                }
                else{
                    editor.putInt("total", total + 1);
                    editor.putString("name_" + total, menu.getMenuName());
                    editor.putFloat("price_" + total, Float.parseFloat(menu.getMenuPrice()));
                    editor.commit();

                }
                Toast.makeText(ActivityMenuList.this, menu.getMenuName() + " added to your cart", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class getMenu extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ActivityMenuList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(DataManager.MENU_URL);


            if (jsonStr != null) {
                int success;
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("r_id", r_id));

                    JSONObject json = jsonParser.makeHttpRequest(DataManager.MENU_URL,
                            "GET", params);

                    Log.d(LOG_TAG,"Menu List: " + json);

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully received product details
                        JSONArray msgObj = json.getJSONArray("menu"); // JSON Array
                        for (int i = 0; i <msgObj.length() ; i++) {
                            JSONObject message = msgObj.getJSONObject(i);

                            String rid = message.getString("r_id");
                            String id = message.getString("m_id");
                            String name = message.getString("m_name");
                            String type = message.getString("m_type");
                            String detail = message.getString("m_detail");
                            String price = message.getString("m_price");
                            //String count = message.getString("message");
                            // message with this pid found
                            ModelMenu addMenu= new ModelMenu(id,rid,name,type,price,detail);
                            menuArrayList.add(addMenu);
                        }

                    }else{

                        Toast.makeText(ActivityMenuList.this, "Menu not found", Toast.LENGTH_SHORT).show();
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
    /**
     * Async task class to get json by making HTTP call
     */
    private class searchMenu extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ActivityMenuList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(DataManager.SEARCH_MENU_URL);


            if (jsonStr != null) {
                int success;
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("search", search));
                    params.add(new BasicNameValuePair("r_id", r_id));

                    JSONObject json = jsonParser.makeHttpRequest(DataManager.SEARCH_MENU_URL,
                            "GET", params);

                    Log.d("Search restaurant",json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully received product details
                        JSONArray msgObj = json.getJSONArray("menu"); // JSON Array
                        for (int i = 0; i <msgObj.length() ; i++) {
                            JSONObject message = msgObj.getJSONObject(i);

                            String rid = message.getString("r_id");
                            String id = message.getString("m_id");
                            String name = message.getString("m_name");
                            String type = message.getString("m_type");
                            String detail = message.getString("m_detail");
                            String price = message.getString("m_price");
                            //String count = message.getString("message");
                            // message with this pid found
                            ModelMenu addMenu= new ModelMenu(id,rid,name,type,price,detail);
                            menuArrayList.add(addMenu);
                        }

                    }else{
                        // product with pid not found
                        //Toast.makeText(ActivityMenuList.this, "Menu not found", Toast.LENGTH_SHORT).show();
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
            case R.id.cart:
                Intent i = new Intent(ActivityMenuList.this, ActivityCart.class);
                startActivity(i);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}



