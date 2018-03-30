package com.hes.easysales.easysales.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.hes.easysales.easysales.Config;

/**
 * Created by sinopsys on 3/31/18.
 */

public class SharedPrefsUtil {

    public static String getStringPref(Context c, String key) {
        SharedPreferences sharedPrefs = c.getSharedPreferences(Config.SH_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(key, Config.DEF_NO_TOKEN);
    }

}


// EOF
