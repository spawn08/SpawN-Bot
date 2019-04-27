package com.spawn.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtility {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferenceUtility sharedPreferenceUtility;
    private static Context mContext;

    public static SharedPreferenceUtility getInstance(Context context) {
        if (sharedPreferenceUtility == null) {
            sharedPreferenceUtility = new SharedPreferenceUtility();
        }
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences("spawnai", Context.MODE_PRIVATE);
        return sharedPreferenceUtility;
    }

    public void storePreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getPreference(String key) {
        return sharedPreferences.getBoolean(key, true);
    }
}
