package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.NativeAds;
import com.x_viria.app.vita.somnificus.core.Remind;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmTime;
import com.x_viria.app.vita.somnificus.core.bill.BillingManager;
import com.x_viria.app.vita.somnificus.service.AlarmService;
import com.x_viria.app.vita.somnificus.service.PlaySoundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class WakeupActivity extends AppCompatActivity {

    private static boolean IS_PREMIUM = false;
    private boolean IS_ALARM_PROCESS_RUNNING = false;
    private BillingManager billingManager;

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

        billingManager = new BillingManager(this);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            billingManager.queryPurchaseAsync(
                    BillingClient.ProductType.SUBS,
                    BillingManager.ProductId.SUBS_PREMIUM,
                    (billingResult, purchased) -> IS_PREMIUM = purchased
            );
        }).start();

        Intent tIntent = getIntent();
        int id = tIntent.getIntExtra("id", -1);

        Intent intent = new Intent(this, PlaySoundService.class);
        Intent intent2 = new Intent(this, AlarmService.class);

        LinearLayout helloView = findViewById(R.id.WakeupActivity__Hello_View);

        Button closeBtn = findViewById(R.id.WakeupActivity__Close_Btn);
        closeBtn.setOnClickListener(v -> finish());

        LocalDateTime nowDate = LocalDateTime.now();

        TextView helloText = findViewById(R.id.WakeupActivity__Hello_Text);
        if (4 < nowDate.getHour() && nowDate.getHour() < 12) {
            helloText.setText(getString(R.string.activity_wakeup__text_good_morning));
        } else if (11 < nowDate.getHour() && nowDate.getHour() < 19) {
            helloText.setText(getString(R.string.activity_wakeup__text_good_afternoon));
        } else {
            helloText.setText(getString(R.string.activity_wakeup__text_good_evening));
        }

        SimpleDateFormat d_format = new SimpleDateFormat(getString(R.string.common__text_date_format__full));
        ((TextView) findViewById(R.id.WakeupActivity__Hello_View__Date)).setText(d_format.format(nowDate));

        LinearLayout stopBtn = findViewById(R.id.WakeupActivity__Stop_Sound_Btn);
        stopBtn.setOnClickListener(v -> {
            this.IS_ALARM_PROCESS_RUNNING = false;
            stopService(intent);
            stopService(intent2);

            if (!IS_PREMIUM) {
                NativeAds nativeAds = new NativeAds(this);
                nativeAds.load(findViewById(R.id.WakeupActivity__NativeAd_Container), "ca-app-pub-6133161179615824/9422697167");
            }

            ((LinearLayout) findViewById(R.id.WakeupActivity__Wakeup_Action_View)).setVisibility(View.GONE);
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

        LinearLayout snoozeBtn = findViewById(R.id.WakeupActivity__Snooze_Btn);
        snoozeBtn.setOnClickListener(v -> {
            stopService(intent);
            stopService(intent2);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);

            AlarmInfo alarmInfo = new AlarmInfo(
                    new AlarmTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0),
                    AlarmInfo.WEEK__ALL,
                    true
            );

            AlarmSchedule alarmSchedule;
            try {
                alarmSchedule = new AlarmSchedule(this);
                JSONObject object = alarmSchedule.getSchedule(id);
                JSONObject objdata = object.getJSONObject("data");
                JSONObject option = objdata.getJSONObject("option");
                boolean opt_giv = option.getBoolean("gra_increase_vol");
                boolean opt_mute = false;
                if (option.has("mute")) {
                    opt_mute = option.getBoolean("mute");
                }
                alarmInfo.setOption(AlarmInfo.OPT__GRA_INCREASE_VOL, opt_giv);
                alarmInfo.setOption(AlarmInfo.OPT__MUTE_VOL, opt_mute);
                alarmSchedule.setNapSchedule(alarmInfo);
                alarmSchedule.sync();
                alarmInfo.showNextTime(getBaseContext());
                JSONObject _object = alarmSchedule.getSchedule(id);
                if (_object.getInt("type") == AlarmSchedule.TYPE__ALARM) {
                    alarmSchedule.setEnable(id, false);
                } else if (object.getInt("type") == AlarmSchedule.TYPE__NAP) {
                    alarmSchedule.removeSchedule(id);
                }
                alarmSchedule.sync();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
            finish();
        });

        new Handler().postDelayed(() -> {
            if (IS_ALARM_PROCESS_RUNNING) {
                stopBtn.callOnClick();
                Remind.MissedAlarm(this, nowDate);
            }
        }, 5 * 60 * 1000);
    }

}