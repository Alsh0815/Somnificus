package com.x_viria.app.vita.somnificus.util.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class SPStorage {

    private final android.content.SharedPreferences SHARED_PREF;

    public SPStorage(Context context) throws NullPointerException {
        this.SHARED_PREF = context.getSharedPreferences("X-VIRIA_SOMNIFICUS_SHARED_PREF", Context.MODE_PRIVATE);
    }

    public boolean getBool(String key, boolean defaultValue) {
        return SHARED_PREF.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return SHARED_PREF.getInt(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return SHARED_PREF.getLong(key, defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return SHARED_PREF.getString(key, defaultValue);
    }

    public void setBool(String key, boolean value) {
        SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = SHARED_PREF.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
