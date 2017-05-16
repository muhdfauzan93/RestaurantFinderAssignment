package com.example.muhdfauzan.myrestaurant.config;

public class DataManager {

    public static String ADMIN_URL = "http://192.168.0.2/mpd/";
    public static String RESTAURANT_URL = ADMIN_URL + "get_all_restaurant.php";
    public static String RESTAURANT_IMAGE_URL = ADMIN_URL + "restaurant/";
    public static String RESTAURANT_SEARCH_URL = ADMIN_URL + "search_restaurant.php";
    public static String MENU_URL = ADMIN_URL + "get_menu.php";
    public static String SEARCH_MENU_URL = ADMIN_URL + "search_menu.php";
    public static String SEND_ORDER_URL = ADMIN_URL + "reservation.php";

    public static String PREF_CHART = "chart";

}
