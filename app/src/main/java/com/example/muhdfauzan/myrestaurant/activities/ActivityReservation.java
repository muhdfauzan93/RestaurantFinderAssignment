package com.example.muhdfauzan.myrestaurant.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.config.DataManager;
import com.example.muhdfauzan.myrestaurant.utils.HttpHandler;
import com.example.muhdfauzan.myrestaurant.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ActivityReservation extends AppCompatActivity {

    private static final String LOG_TAG = ActivityReservation.class.getSimpleName();
    private String r_id;
    private EditText name,noOfPeople, phoneNo,email,date,time,comment;
    private String r_name,r_noOfPeople, r_phoneNo, r_email,r_date,r_time,r_comment;
    private TextView totalOrder,tv_totalPrice;
    private Button btn_send;
    private float totalPrice = 0;
    String listItem = null;
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle("Reservation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

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
        totalPrice();



    }

    private void initialize(){
        name = (EditText)findViewById(R.id.et_Name);
        noOfPeople = (EditText)findViewById(R.id.et_no_of_people);
        phoneNo = (EditText)findViewById(R.id.et_phone_no);
        email = (EditText)findViewById(R.id.et_email);
        date = (EditText)findViewById(R.id.et_date);
        time = (EditText)findViewById(R.id.et_time);
        comment = (EditText)findViewById(R.id.et_comment);
        totalOrder = (TextView)findViewById(R.id.tv_total_order);
        tv_totalPrice = (TextView)findViewById(R.id.tv_total_price);
        btn_send = (Button)findViewById(R.id.btnSend);


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r_name = name.getText().toString().trim();
                r_noOfPeople = noOfPeople.getText().toString().trim();
                r_phoneNo = phoneNo.getText().toString().trim();
                r_email = email.getText().toString().trim();
                r_date = date.getText().toString().trim();
                r_time = time.getText().toString().trim();
                r_comment = comment.getText().toString().trim();

                Log.d("Reservation",r_name + "|" + noOfPeople + "|" + r_phoneNo + "|" + r_email + "|" + r_date + "|" + r_time + "|" + r_comment);
                if(r_name.length() > 0 && r_noOfPeople.length()>0 && r_phoneNo.length()>0 && r_email.length()>0 &&
                        r_date.length()>0 && r_time.length()>0){
                        new sendOrder().execute();

                }
                else
                    Toast.makeText(ActivityReservation.this, "Please fill out all required fields ", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void totalPrice(){
        SharedPreferences sharedpreferences = getSharedPreferences(DataManager.PREF_CHART, Context.MODE_PRIVATE);

        int total = sharedpreferences.getInt("total", 0);
        for (int i = 0; i <total ; i++) {
            String name = sharedpreferences.getString("name_" + i, null);
            Float price = sharedpreferences.getFloat("price_" + i, 0);
            Log.d("Menu List:", "Total: " + total + "|" + "Name: " + name + " | " + "Price: " + price);
            totalPrice = totalPrice + price;
            if (listItem == null)
                listItem = name;
            else
                listItem = listItem + "\n" + name;

            Log.d("Total Price:", "Total Price:" + totalPrice);
            totalOrder.setText(listItem);
            tv_totalPrice.setText("Total Price: RM" + String.valueOf(totalPrice));
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class sendOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ActivityReservation.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(DataManager.SEND_ORDER_URL);


            if (jsonStr != null) {
                int success;
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("r_id", r_id));
                    params.add(new BasicNameValuePair("name", r_name));
                    params.add(new BasicNameValuePair("phone_no", r_phoneNo));
                    params.add(new BasicNameValuePair("email", r_email));
                    params.add(new BasicNameValuePair("noPeople", r_noOfPeople));
                    params.add(new BasicNameValuePair("date", r_date));
                    params.add(new BasicNameValuePair("time", r_time));
                    params.add(new BasicNameValuePair("totalPrice", String.valueOf(totalPrice)));
                    params.add(new BasicNameValuePair("comment", r_comment));

                    JSONObject json = jsonParser.makeHttpRequest(DataManager.SEND_ORDER_URL,
                            "POST", params);

                    Log.d("Send Reservation",json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully received product details
                        //JSONArray msgObj = json.getJSONArray("menu"); // JSON Array
                        Log.d("Status", "Order send");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Order Send ",
                                        Toast.LENGTH_LONG)
                                        .show();
                                Intent i = new Intent(ActivityReservation.this, ActivityMain.class);
                                startActivity(i);
                            }
                        });

                    }else{
                        Log.d("Status", "Order fail");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Order Fail",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
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
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
