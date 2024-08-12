package com.x_viria.app.vita.somnificus;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class Somnificus extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
