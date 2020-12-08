package com.spawn.ai.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtility {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferenceUtility sharedPreferenceUtility;

    private SharedPreferenceUtility() {

    }

    public static SharedPreferenceUtility getInstance(Context context) {
        if (sharedPreferenceUtility == null) {
            sharedPreferenceUtility = new SharedPreferenceUtility();
        }
        sharedPreferences = context.getSharedPreferences("spawnai", Context.MODE_PRIVATE);
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

    public void storeStringPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringPreference(String key) {
        return sharedPreferences.getString(key, "en");
    }
}
