package com.app.hidroponik.data;

public class Constant {

    public static String WEB_URL = "http://store.hidroponikpedia.com/";
    public static final String SECURITY_CODE = "kZVN9qUWW2sxbYicoueq5mtH8WR25nZAUwiGS1kF4omZck9USjBIOQLf9nVk26eOlcjsRJgbpBc452sWkc7i2dTYmsf1giyVUS0j";
    public static int OPEN_COUNTER = 50;


    public static int NEWS_PER_REQUEST = 10;
    public static int PRODUCT_PER_REQUEST = 10;
    public static int NOTIFICATION_PAGE = 20;
    public static int WISHLIST_PAGE = 20;

    public static int LOAD_IMAGE_NOTIF_RETRY = 3;

    public static String getURLimgProduct(String file_name) {
        return WEB_URL + "uploads/product/" + file_name;
    }

    public static String getURLimgNews(String file_name) {
        return WEB_URL + "uploads/news/" + file_name;
    }

    public static String getURLimgCategory(String file_name) {
        return WEB_URL + "uploads/category/" + file_name;
    }

}
