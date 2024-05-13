package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPKey;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class SettingActivity extends AppCompatActivity {

    private String getVersionName() throws PackageManager.NameNotFoundException {
        String packageName = getPackageName();
        PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        return packageInfo.versionName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SPStorage sps = new SPStorage(this);

        LinearLayout AlarmVibrate_L = findViewById(R.id.SettingActivity__AlarmVibrate_L);
        SwitchMaterial AlarmVibrate_SW = findViewById(R.id.SettingActivity__AlarmVibrate_SW);
        AlarmVibrate_L.setOnClickListener(v -> AlarmVibrate_SW.setChecked(!AlarmVibrate_SW.isChecked()));
        AlarmVibrate_SW.setChecked(sps.getBool(SPKey.KEY__SETTINGS_ALARM_VIBRATE, SPDefault.SETTINGS_ALARM_VIBRATE));
        AlarmVibrate_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(SPKey.KEY__SETTINGS_ALARM_VIBRATE, isChecked));

        LinearLayout TimerVibrate_L = findViewById(R.id.SettingActivity__TimerVibrate_L);
        SwitchMaterial TimerVibrate_SW = findViewById(R.id.SettingActivity__TimerVibrate_SW);
        TimerVibrate_L.setOnClickListener(v -> TimerVibrate_SW.setChecked(!TimerVibrate_SW.isChecked()));
        TimerVibrate_SW.setChecked(sps.getBool(SPKey.KEY__SETTINGS_TIMER_VIBRATE, SPDefault.SETTINGS_TIMER_VIBRATE));
        TimerVibrate_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(SPKey.KEY__SETTINGS_TIMER_VIBRATE, isChecked));

        LinearLayout Version_L = findViewById(R.id.SettingActivity__Version_L);
        Version_L.setOnClickListener(v -> {
            try {
                Toast.makeText(getApplicationContext(), "ver." + getVersionName(), Toast.LENGTH_SHORT).show();
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        TextView Version_TX = findViewById(R.id.SettingActivity__Version_Text);
        try {
            Version_TX.setText(getVersionName());
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}