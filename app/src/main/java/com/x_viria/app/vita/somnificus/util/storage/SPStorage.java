package com.x_viria.app.vita.somnificus.util.storage;

import android.content.Context;

public class SPStorage {

    private final android.content.SharedPreferences SHARED_PREF;

    public SPStorage(Context context) {
        this.SHARED_PREF = context.getSharedPreferences("X-VIRIA_SOMNIFICUS_SHARED_PREF", Context.MODE_PRIVATE);
    }

    public boolean getBool(String key, boolean defaultValue) {
        return SHARED_PREF.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return SHARED_PREF.getInt(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return SHARED_PREF.getString(key, defaultValue);
    }

    public void setBool(String key, boolean value) {
        android.content.SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setInt(String key, int value) {
        android.content.SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setString(String key, String value) {
        android.content.SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
