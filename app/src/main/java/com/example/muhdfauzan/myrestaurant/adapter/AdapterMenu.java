package com.example.muhdfauzan.myrestaurant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muhdfauzan.myrestaurant.R;
import com.example.muhdfauzan.myrestaurant.model.ModelMenu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.MyViewHolder>   {

    private String LOG_TAG = AdapterRestaurant.class.getSimpleName();
    private ArrayList<ModelMenu> menuArrayList;
    private Context context;
    private static String today;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name, tv_price, tv_detail, tv_cat;

        public MyViewHolder(View view) {
            super(view);

            tv_name = (TextView) view.findViewById(R.id.tv_name);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_detail = (TextView) view.findViewById(R.id.tv_detail);
            tv_cat = (TextView) view.findViewById(R.id.tv_cat);

        }
    }
    public AdapterMenu(Context context, ArrayList<ModelMenu> menuArrayList) {
        this.menuArrayList = menuArrayList;
        this.context = context;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_menu, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        ModelMenu menu = menuArrayList.get(position);
        holder.setIsRecyclable(false);

        holder.tv_name.setText(menu.getMenuName());
        holder.tv_price.setText(menu.getMenuPrice());
        holder.tv_cat.setText(menu.getMenuType());
        holder.tv_detail.setText(menu.getMenuDetail());

        //Glide.with(context).load(DataManager.RESTAURANT_IMAGE_URL+restaurant.getImage()+".png").into(holder.iv_image);

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
        return menuArrayList.size();
    }
}


