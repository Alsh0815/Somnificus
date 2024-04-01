package com.x_viria.app.vita.somnificus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.x_viria.app.vita.somnificus.fragment.alarm.AlarmFragment;
import com.x_viria.app.vita.somnificus.fragment.etc.EtcFragment;
import com.x_viria.app.vita.somnificus.fragment.sleep.SleepFragment;
import com.x_viria.app.vita.somnificus.fragment.stopwatch.StopwatchFragment;
import com.x_viria.app.vita.somnificus.fragment.timer.TimerFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView bottomNavigationView;

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        setFragment(new AlarmFragment());

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 各メニュー項目のクリックイベント処理
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
    }
}