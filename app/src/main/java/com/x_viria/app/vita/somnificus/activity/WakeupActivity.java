package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.Remind;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.ui.NativeAdBuilder;
import com.x_viria.app.vita.somnificus.service.AlarmService;
import com.x_viria.app.vita.somnificus.service.PlaySoundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WakeupActivity extends AppCompatActivity {

    private boolean IS_ALARM_PROCESS_RUNNING = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Log.d("onCreate", "called");

        this.IS_ALARM_PROCESS_RUNNING = true;

        Intent tIntent = getIntent();
        int id = tIntent.getIntExtra("id", -1);

        Intent intent = new Intent(this, PlaySoundService.class);
        Intent intent2 = new Intent(this, AlarmService.class);

        LinearLayout helloView = findViewById(R.id.WakeupActivity__Hello_View);
        NativeAdBuilder nativeAdBuilder = new NativeAdBuilder(this, this);
        nativeAdBuilder.setLayout(R.id.WakeupActivity__NativeAd_Container);
        nativeAdBuilder.create(
                "ca-app-pub-6133161179615824/9422697167",
                LayoutInflater.from(this).inflate(R.layout.nativead_view, null)
        );
        nativeAdBuilder.loadAd();

        Button closeBtn = findViewById(R.id.WakeupActivity__Close_Btn);
        closeBtn.setOnClickListener(v -> finish());

        Date nowDate = new Date();

        TextView helloText = findViewById(R.id.WakeupActivity__Hello_Text);
        if (4 < nowDate.getHours() && nowDate.getHours() < 12) {
            helloText.setText(getString(R.string.activity_wakeup__text_good_morning));
        } else if (11 < nowDate.getHours() && nowDate.getHours() < 19) {
            helloText.setText(getString(R.string.activity_wakeup__text_good_afternoon));
        } else {
            helloText.setText(getString(R.string.activity_wakeup__text_good_evening));
        }

        SimpleDateFormat d_format = new SimpleDateFormat(getString(R.string.common__text_date_format__full));
        ((TextView) findViewById(R.id.WakeupActivity__Hello_View__Date)).setText(d_format.format(nowDate));

        LinearLayout stopSound = findViewById(R.id.WakeupActivity__Stop_Sound_Btn);
        stopSound.setOnClickListener(v -> {
            this.IS_ALARM_PROCESS_RUNNING = false;
            stopService(intent);
            stopService(intent2);

            stopSound.setVisibility(View.GONE);
            helloView.setVisibility(View.VISIBLE);
            helloView.setAlpha(0.0f);
            ObjectAnimator animation = ObjectAnimator.ofFloat(helloView, "alpha", 1.0f);
            animation.setDuration(1000);
            animation.start();

            try {
                AlarmSchedule alarmSchedule = new AlarmSchedule(WakeupActivity.this);
                alarmSchedule.sync();
                JSONObject object = alarmSchedule.getSchedule(id);
                if (object.getInt("type") == AlarmSchedule.TYPE__ALARM) {
                    alarmSchedule.setEnable(id, false);
                } else if (object.getInt("type") == AlarmSchedule.TYPE__NAP) {
                    alarmSchedule.removeSchedule(id);
                }
                alarmSchedule.sync();

                long untilNext = alarmSchedule.getTimeUntilNext();
                if (30 * 60 * 1000 <= untilNext) {
                    Remind.SaveSleepDataNotification(this);
                }
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        new Handler().postDelayed(() -> {
            if (IS_ALARM_PROCESS_RUNNING) {
                stopSound.callOnClick();
                Remind.MissedAlarm(this, nowDate);
            }
        }, 5 * 60 * 1000);
    }

}