package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmTime;
import com.x_viria.app.vita.somnificus.util.Theme;

import org.json.JSONException;

import java.io.IOException;

public class SetNapActivity extends AppCompatActivity {

    private AlarmInfo getAlarmInfo() {
        NumberPicker np_m = findViewById(R.id.SetNapActivity__NumPicker_M);
        SwitchMaterial opt_GIV_SW = findViewById(R.id.SetNapActivity__Switch_Opt_GIV);
        SwitchMaterial opt_Mute_SW = findViewById(R.id.SetNapActivity__Switch_Opt_Mute);
        int min = np_m.getValue() % 60;
        int hour = np_m.getValue() / 60;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hour);
        calendar.add(Calendar.MINUTE, min);
        AlarmInfo alarmInfo = new AlarmInfo(
                new AlarmTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0),
                AlarmInfo.WEEK__ALL,
                true
        );
        alarmInfo.setOption(AlarmInfo.OPT__GRA_INCREASE_VOL, opt_GIV_SW.isChecked());
        alarmInfo.setOption(AlarmInfo.OPT__MUTE_VOL, opt_Mute_SW.isChecked());
        return alarmInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nap);

        ImageView back_btn = findViewById(R.id.SetNapActivity__Btn_Back);
        back_btn.setOnClickListener(v -> finish());

        NumberPicker np_m = findViewById(R.id.SetNapActivity__NumPicker_M);
        np_m.setMaxValue(120);
        np_m.setMinValue(5);
        np_m.setValue(30);

        SwitchMaterial opt_GIV_SW = findViewById(R.id.SetNapActivity__Switch_Opt_GIV);
        LinearLayout opt_GIV = findViewById(R.id.SetNapActivity__View_Opt_GIV);
        opt_GIV.setOnClickListener(v -> opt_GIV_SW.setChecked(!opt_GIV_SW.isChecked()));

        SwitchMaterial opt_Mute_SW = findViewById(R.id.SetNapActivity__Switch_Opt_Mute);
        opt_Mute_SW.setOnCheckedChangeListener((buttonView, isChecked) -> opt_GIV_SW.setEnabled(!isChecked));
        LinearLayout opt_Mute = findViewById(R.id.SetNapActivity__View_Opt_Mute);
        opt_Mute.setOnClickListener(v -> {
            opt_Mute_SW.setChecked(!opt_Mute_SW.isChecked());
            opt_GIV.setClickable(!opt_Mute_SW.isChecked());
        });

        TextView save_btn = findViewById(R.id.SetNapActivity__Btn_Save);
        save_btn.setOnClickListener(v -> {
            try {
                AlarmInfo alarmInfo = getAlarmInfo();
                AlarmSchedule alarmSchedule = new AlarmSchedule(this);
                alarmSchedule.setNapSchedule(alarmInfo);
                alarmSchedule.sync();
                alarmInfo.showNextTime(getBaseContext());
                finish();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}