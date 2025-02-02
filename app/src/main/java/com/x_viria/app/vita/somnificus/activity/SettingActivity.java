package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.Theme;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class SettingActivity extends AppCompatActivity {

    private String getVersionName() throws PackageManager.NameNotFoundException {
        String packageName = getPackageName();
        PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
        return packageInfo.versionName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SPStorage sps = new SPStorage(this);

        LinearLayout AlarmVibrate_L = findViewById(R.id.SettingActivity__AlarmVibrate_L);
        SwitchMaterial AlarmVibrate_SW = findViewById(R.id.SettingActivity__AlarmVibrate_SW);
        AlarmVibrate_L.setOnClickListener(v -> AlarmVibrate_SW.setChecked(!AlarmVibrate_SW.isChecked()));
        AlarmVibrate_SW.setChecked(sps.getBool(Config.KEY__SETTINGS_ALARM_VIBRATE, SPDefault.SETTINGS_ALARM_VIBRATE));
        AlarmVibrate_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(Config.KEY__SETTINGS_ALARM_VIBRATE, isChecked));

        LinearLayout TimerSound_L = findViewById(R.id.SettingActivity__TimerSound_L);
        SwitchMaterial TimerSound_SW = findViewById(R.id.SettingActivity__TimerSound_SW);
        TimerSound_L.setOnClickListener(v -> TimerSound_SW.setChecked(!TimerSound_SW.isChecked()));
        TimerSound_SW.setChecked(sps.getBool(Config.KEY__SETTINGS_TIMER_SOUND, SPDefault.SETTINGS_TIMER_SOUND));
        TimerSound_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(Config.KEY__SETTINGS_TIMER_SOUND, isChecked));

        LinearLayout TimerVibrate_L = findViewById(R.id.SettingActivity__TimerVibrate_L);
        SwitchMaterial TimerVibrate_SW = findViewById(R.id.SettingActivity__TimerVibrate_SW);
        TimerVibrate_L.setOnClickListener(v -> TimerVibrate_SW.setChecked(!TimerVibrate_SW.isChecked()));
        TimerVibrate_SW.setChecked(sps.getBool(Config.KEY__SETTINGS_TIMER_VIBRATE, SPDefault.SETTINGS_TIMER_VIBRATE));
        TimerVibrate_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(Config.KEY__SETTINGS_TIMER_VIBRATE, isChecked));

        LinearLayout RemindSaveSD_L = findViewById(R.id.SettingActivity__Remind_SaveSD_L);
        SwitchMaterial RemindSaveSD_SW = findViewById(R.id.SettingActivity__Remind_SaveSD_SW);
        RemindSaveSD_L.setOnClickListener(v -> RemindSaveSD_SW.setChecked(!RemindSaveSD_SW.isChecked()));
        RemindSaveSD_SW.setChecked(sps.getBool(Config.KEY__SETTINGS_REMIND_SAVESD, SPDefault.SETTINGS_REMIND_SAVESD));
        RemindSaveSD_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(Config.KEY__SETTINGS_REMIND_SAVESD, isChecked));

        LinearLayout RemindSetAlarm_L = findViewById(R.id.SettingActivity__Remind_SetAlarm_L);
        SwitchMaterial RemindSetAlarm_SW = findViewById(R.id.SettingActivity__Remind_SetAlarm_SW);
        RemindSetAlarm_L.setOnClickListener(v -> RemindSetAlarm_SW.setChecked(!RemindSetAlarm_SW.isChecked()));
        RemindSetAlarm_SW.setChecked(sps.getBool(Config.KEY__SETTINGS_REMIND_SET_ALARM, SPDefault.SETTINGS_REMIND_SET_ALARM));
        RemindSetAlarm_SW.setOnCheckedChangeListener((buttonView, isChecked) -> sps.setBool(Config.KEY__SETTINGS_REMIND_SET_ALARM, isChecked));

        final boolean[] isFirst = {true};
        Spinner SelectTheme_Spinner = findViewById(R.id.SettingActivity__Theme_Select_Spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Config.Theme.LABELS);
        SelectTheme_Spinner.setAdapter(arrayAdapter);
        SelectTheme_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst[0]) {
                    isFirst[0] = false;
                    return;
                }
                int style_id = Config.Theme.VALUES[position];
                new SPStorage(SettingActivity.this).setInt(Config.KEY__SETTINGS_APP_THEME, style_id);
                setTheme(style_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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