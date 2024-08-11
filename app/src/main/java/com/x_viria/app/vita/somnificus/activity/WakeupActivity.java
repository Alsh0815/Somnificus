package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.service.AlarmService;
import com.x_viria.app.vita.somnificus.service.PlaySoundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WakeupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("onCreate", "called");

        Intent tIntent = getIntent();
        int id = tIntent.getIntExtra("id", -1);

        Intent intent = new Intent(this, PlaySoundService.class);
        Intent intent2 = new Intent(this, AlarmService.class);
        LinearLayout stopSound = findViewById(R.id.WakeupActivity__Stop_Sound_Btn);
        stopSound.setOnClickListener(v -> {
            stopService(intent);
            stopService(intent2);
            try {
                AlarmSchedule alarmSchedule = new AlarmSchedule(WakeupActivity.this);
                alarmSchedule.sync();
                JSONObject object = alarmSchedule.getSchedule(id);
                if (object.getInt("type") == AlarmSchedule.TYPE__NAP) {
                    alarmSchedule.removeSchedule(id);
                }
                alarmSchedule.sync();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            finish();
        });
    }

}