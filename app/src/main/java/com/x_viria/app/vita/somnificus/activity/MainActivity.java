package com.x_viria.app.vita.somnificus.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.bill.BillingManager;
import com.x_viria.app.vita.somnificus.fragment.main.alarm.AlarmFragment;
import com.x_viria.app.vita.somnificus.fragment.main.etc.EtcFragment;
import com.x_viria.app.vita.somnificus.fragment.main.sleep.SleepFragment;
import com.x_viria.app.vita.somnificus.fragment.main.stopwatch.StopwatchFragment;
import com.x_viria.app.vita.somnificus.fragment.main.timer.TimerFragment;
import com.x_viria.app.vita.somnificus.receiver.StartupReceiver;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class MainActivity extends AppCompatActivity {

    public static final String PUT_EXTRA__SET_FRAGMENT = "setFragment";
    public static final int SET_FRAGMENT__TIMER = 1;

    private FirebaseAnalytics mFirebaseAnalytics;

    private BottomNavigationView bottomNavigationView;
    private int fragmentNowPage = 1;

    private BillingManager BILLING_MANAGER;
    private int selectedItemId;

    private void setFragment(Fragment fragment, int page) {
        // Set a fragment.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragmentNowPage < page) {
            transaction.setCustomAnimations(R.anim.fragment_enter_to_right, R.anim.fragment_exit_to_left);
        } else if (page < fragmentNowPage) {
            transaction.setCustomAnimations(R.anim.fragment_enter_to_left, R.anim.fragment_exit_to_right);
        }
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();

        fragmentNowPage = page;

        // Fix the bottom margin of the fragment.
        /*
        int bnv_height = bottomNavigationView.getHeight();
        View fragment_container = findViewById(R.id.fragment_container);
        ViewGroup.LayoutParams lp = fragment_container.getLayoutParams();
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
        mlp.setMargins(mlp.leftMargin, mlp.topMargin, mlp.rightMargin, bnv_height);
        fragment_container.setLayoutParams(mlp);
         */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        getApplicationContext().registerReceiver(new StartupReceiver(), new IntentFilter(Intent.ACTION_LOCKED_BOOT_COMPLETED));

        int iA_fragment = getIntent().getIntExtra(PUT_EXTRA__SET_FRAGMENT, 0);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Handler handle = new Handler();
        handle.postDelayed(() -> setFragment(new AlarmFragment(), 1), 100);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_alarm:
                    setFragment(new AlarmFragment(), 1);
                    return true;
                case R.id.menu_timer:
                    setFragment(new TimerFragment(), 2);
                    return true;
                case R.id.menu_stopwatch:
                    setFragment(new StopwatchFragment(), 3);
                    return true;
                case R.id.menu_sleep:
                    setFragment(new SleepFragment(), 4);
                    return true;
                case R.id.menu_etc:
                    setFragment(new EtcFragment(), 5);
                    return true;
            }
            return false;
        });

        if (!new SPStorage(this).getBool(Config.KEY__TUTORIAL_COMPLETED, false)) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        } else {
            if (
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                String[] permissions = {
                        Manifest.permission.POST_NOTIFICATIONS
                };
                ActivityCompat.requestPermissions(this, permissions, 0xF00);
            }
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0xfff);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }

        (new Handler()).postDelayed(() -> {
            if (iA_fragment == 1) {
                setFragment(new TimerFragment(), 2);
                Menu menu = bottomNavigationView.getMenu();
                for (int i = 0, size = menu.size(); i < size; i++) {
                    MenuItem item = menu.getItem(i);
                    if (item.getItemId() == R.id.menu_timer) item.setChecked(true);
                }
            }
        }, 500);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0xF00) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted Permission: " + permissions[i], Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Rejected Permission: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}