package com.hes.easysales.easysales;

/**
 * Created by sinopsys on 3/16/18.
 */

public class Config {

    public static final String URL_CORE = "http://gcsales.ru/";
    //    http://gcsales.ru/api/shops/1?category=&page=2
    public static final String URL_LOGIN = URL_CORE + "auth/login/";
    public static final String URL_REGISTER = URL_CORE + "auth/register/";
    public static final String URL_SALES_SHOP = URL_CORE + "api/shops/";
    public static final String URL_SHOPLIST = URL_CORE + "api/shoplist/";
    public static final String URL_SHOPLISTS_PREVIEW = URL_CORE + "api/shoplist?mode=preview";
    public static final String URL_SHOPLISTS = URL_CORE + "api/shoplist?mode=full";
    public static final String URL_ITEMS_ON_PAGE = "&page=";
    public static final String URL_ITEMS_IN_CATEGRY = "?category=";
    public static final String URL_CATEGORIES = "categories";
    //    http://gcsales.ru/api/shoplist/18/additem?id=188
    public static final String URL_SL_ADD_ITEM = "additem?id=";
    public static final String URL_SL_ADD_CUSTOM_ITEM = "additem?custom=";
    public static final String URL_SL_DELETE_ITEM = "deleteitem?id=";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String TAG_VOLLEY_ERROR = "VOLLEY";
    public static final String REQUESTS_CONTENT_TYPE = "application/json; charset=utf-8";
    public static final String BAD_API_AUTH_RESPONSE = "Wrong username/password.";
    public static final String SH_PREFS_NAME = "easy_sales_token";
    public static final String KEY_TOKEN = "user_token";
    public static final String DEF_NO_TOKEN = "NULL";
    public static final String KEY_ITEMS = "all_items";
    public static final String KEY_SHOPLIST_ITEMS = "shoplist_items";
    public static final String KEY_CURRENT_SHOPLIST = "com.hes.easysales.easysales.ShopList.";
    public static final int MAX_LEN_PREVIEW = 20;
    public static final int MAX_ITEMS_PREVIEW = 6;
}


// EOF
