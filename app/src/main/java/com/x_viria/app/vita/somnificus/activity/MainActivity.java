package com.x_viria.app.vita.somnificus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.fragment.main.alarm.AlarmFragment;
import com.x_viria.app.vita.somnificus.fragment.main.etc.EtcFragment;
import com.x_viria.app.vita.somnificus.fragment.main.sleep.SleepFragment;
import com.x_viria.app.vita.somnificus.fragment.main.stopwatch.StopwatchFragment;
import com.x_viria.app.vita.somnificus.fragment.main.timer.TimerFragment;
import com.x_viria.app.vita.somnificus.util.storage.SPKey;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

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

        if (!new SPStorage(this).getBool(SPKey.KEY__TUTORIAL_COMPLETED, false)) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        }

    }

}