package com.x_viria.app.vita.somnificus.activity;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.Alarm;
import com.x_viria.app.vita.somnificus.fragment.alarm.AlarmFragment;
import com.x_viria.app.vita.somnificus.fragment.etc.EtcFragment;
import com.x_viria.app.vita.somnificus.fragment.sleep.SleepFragment;
import com.x_viria.app.vita.somnificus.fragment.stopwatch.StopwatchFragment;
import com.x_viria.app.vita.somnificus.fragment.timer.TimerFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView bottomNavigationView;

    private void setFragment(Fragment fragment) {
        // Set a fragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Fix the bottom margin of the fragment.
        int bnv_height = bottomNavigationView.getHeight();
        View fragment_container = findViewById(R.id.fragment_container);
        ViewGroup.LayoutParams lp = fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
        mlp.setMargins(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, bnv_height);
        fragment_container.setLayoutParams(mlp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Handler handle = new Handler();
        handle.postDelayed(() -> setFragment(new AlarmFragment()), 100);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_alarm:
                    setFragment(new AlarmFragment());
                    return true;
                case R.id.menu_timer:
                    setFragment(new TimerFragment());
                    return true;
                case R.id.menu_stopwatch:
                    setFragment(new StopwatchFragment());
                    return true;
                case R.id.menu_sleep:
                    setFragment(new SleepFragment());
                    return true;
                case R.id.menu_etc:
                    setFragment(new EtcFragment());
                    return true;
            }
            return false;
        });

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Alarm.setAlarm(this);
        }
    }

}