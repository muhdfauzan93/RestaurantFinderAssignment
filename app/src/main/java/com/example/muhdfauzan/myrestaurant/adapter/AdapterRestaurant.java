package com.example.muhdfauzan.myrestaurant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.config.DataManager;
import com.example.muhdfauzan.myrestaurant.model.ModelRestaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AdapterRestaurant extends RecyclerView.Adapter<AdapterRestaurant.MyViewHolder>  {

    private String LOG_TAG = AdapterRestaurant.class.getSimpleName();
    private ArrayList<ModelRestaurant> restaurantsArrayList;
    private Context context;
    private static String today;
    private ImageView[] imagePick;
    private TextView[] cardNameList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_address, tv_open_hour;
        private ImageView iv_image;

        public MyViewHolder(View view) {
            super(view);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            tv_open_hour = (TextView) view.findViewById(R.id.tv_open_hour);
            iv_image = (ImageView) view.findViewById(R.id.iv_restaurant_img);
        }
    }
    public AdapterRestaurant(Context context, ArrayList<ModelRestaurant> restaurantsArrayList) {
        this.restaurantsArrayList = restaurantsArrayList;
        this.context = context;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_restaurant, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        ModelRestaurant restaurant = restaurantsArrayList.get(position);
        holder.setIsRecyclable(false);

        holder.tv_name.setText(restaurant.getName());
        holder.tv_address.setText(restaurant.getAddress());
        holder.tv_open_hour.setText(restaurant.getOpenhour());

        Glide.with(context).load(DataManager.RESTAURANT_IMAGE_URL+restaurant.getImage()+".png").into(holder.iv_image);


    }


    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }


    @Override
    public int getItemCount() {
        return restaurantsArrayList.size();
    }
}


