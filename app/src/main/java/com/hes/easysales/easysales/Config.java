package com.hes.easysales.easysales;

/**
 * Created by sinopsys on 3/16/18.
 */

public class Config {

    public static final String URL_CORE = "http://46.17.44.125:8080";
    public static final String URL_LOGIN = URL_CORE + "/auth/login";
    public static final String URL_SALES_SHOP_1 = URL_CORE + "/api/shops/dixy";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String TAG_VOLLEY_ERROR = "VOLLEY";
    public static final String REQUESTS_CONTENT_TYPE = "application/json; charset=utf-8";
    public static final String BAD_API_AUTH_RESPONSE = "Wrong username/password.";
    public static final String SH_PREFS_NAME = "easy_sales_token";
    public static final String KEY_TOKEN = "user_token";
    public static final String DEF_NO_TOKEN = "NULL";
}


// EOF
