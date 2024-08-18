package com.x_viria.app.vita.somnificus.activity;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.util.Unit;

import org.json.JSONException;

import java.util.Date;
import java.util.List;

public class SleepDurationActivity extends AppCompatActivity {

    private int SD_INDEX = 0;
    private int SD_PERIOD = SleepDurationManager.Period.WEEK;

    private void loadSD(int period, int index) throws JSONException {
        LinearLayout LL__SDList = findViewById(R.id.SleepDurationActivity__SDList);
        LL__SDList.removeAllViews();

        TypedValue rippleEffect = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        SleepDurationManager sdm = new SleepDurationManager(this);
        List<SleepDurationInfo> list = sdm.get(period, index);

        list.sort((o1, o2) -> {
            if (o1.getBedTime() < o2.getBedTime()) {
                return 1;
            } else if (o1.getBedTime() == o2.getBedTime()) {
                return Long.compare(o2.getWakeupTime(), o1.getWakeupTime());
            } else {
                return -1;
            }
        });

        for (SleepDurationInfo info : list) {
            int padding1 = Unit.dp2px(this, 8.0f);
            int padding2 = Unit.dp2px(this, 4.0f);
            LinearLayout parent = new LinearLayout(this);
            parent.setBackgroundResource(rippleEffect.resourceId);
            parent.setClickable(true);
            parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setPadding(padding1, padding1, padding1, padding1);
            parent.setOnClickListener(v -> {
                new AlertDialog.Builder(this, R.style.SomnificusAlertDialogTheme)
                        .setCancelable(false)
                        .setTitle(R.string.fragment_main_alarm__dialog_del_alarm_title)
                        .setMessage(R.string.activity_sleep_duration__dialog_del_sd_msg)
                        .setNegativeButton(R.string.common__text_no, null)
                        .setPositiveButton(R.string.common__text_yes, (dialog, which) -> {
                            try {
                                sdm.remove(info.ID);
                                loadSD(period, index);
                                BackupManager backupManager = new BackupManager(this);
                                backupManager.dataChanged();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .show();
            });

            LinearLayout head = new LinearLayout(this);
            head.setGravity(Gravity.CENTER_VERTICAL);
            head.setOrientation(LinearLayout.HORIZONTAL);

            ImageView icon = new ImageView(this);
            icon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_sleep));
            icon.setImageTintList(ColorStateList.valueOf(getColor(R.color.secondaryTextColor)));
            icon.setLayoutParams(new LinearLayout.LayoutParams(
                    Unit.dp2px(this, 14),
                    Unit.dp2px(this, 14)
            ));

            Date d_from = new Date();
            d_from.setTime(info.getBedTime());
            SimpleDateFormat f_from = new SimpleDateFormat(getString(R.string.activity_sleep_duration__date_format_from));
            Date d_to = new Date();
            d_to.setTime(info.getWakeupTime());
            SimpleDateFormat f_to = new SimpleDateFormat(getString(R.string.activity_sleep_duration__date_format_to));
            TextView date = new TextView(this);
            date.setPadding(padding2, padding2, padding2, padding2);
            date.setText(String.format("%s - %s", f_from.format(d_from), f_to.format(d_to)));
            date.setTextColor(getColor(R.color.secondaryTextColor));
            date.setTextSize(COMPLEX_UNIT_DIP, 14);

            head.addView(icon);
            head.addView(date);

            long duration = info.getWakeupTime() - info.getBedTime();
            long sec = duration / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;

            TextView duration_text = new TextView(this);
            duration_text.setPadding(padding2, padding2, padding2, padding2);
            duration_text.setText(String.format(getString(R.string.fragment_main_sleep__text_sd_time), hour, min));
            duration_text.setTextColor(getColor(R.color.primaryTextColor));
            duration_text.setTextSize(COMPLEX_UNIT_DIP, 18);

            parent.addView(head);
            parent.addView(duration_text);

            LL__SDList.addView(parent);
        }
    }

    private void refreshUI() {
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        if (SD_PERIOD == SleepDurationManager.Period.WEEK) {
            begin.add(Calendar.WEEK_OF_YEAR, -1 * SD_INDEX);
            begin.set(Calendar.DAY_OF_WEEK, 1);
            end.add(Calendar.WEEK_OF_YEAR, -1 * SD_INDEX);
            end.set(Calendar.DAY_OF_WEEK, 7);
        }
        int primaryTintColor = ContextCompat.getColor(this, R.color.primaryTextColor);
        int secondaryTintColor = ContextCompat.getColor(this, R.color.primaryInactiveColor);
        TextView TV_SD_Period = findViewById(R.id.SleepDurationActivity__SD_Period_Text);
        ImageView IV_Btn__Next = findViewById(R.id.SleepDurationActivity__Next_Btn);
        if (SD_INDEX == 0) {
            IV_Btn__Next.setClickable(false);
            IV_Btn__Next.setImageTintList(ColorStateList.valueOf(secondaryTintColor));
        } else {
            IV_Btn__Next.setClickable(true);
            IV_Btn__Next.setImageTintList(ColorStateList.valueOf(primaryTintColor));
        }
        String f;
        SimpleDateFormat sdf1 = new SimpleDateFormat(getString(R.string.activity_sleep_duration__sd_period_format_1));
        SimpleDateFormat sdf2 = new SimpleDateFormat(getString(R.string.activity_sleep_duration__sd_period_format_2));
        if (begin.get(Calendar.MONTH) == end.get(Calendar.MONTH)) {
            f = String.format(
                    getString(R.string.activity_sleep_duration__sd_period_text_format),
                    sdf1.format(begin.getTime()),
                    sdf2.format(end.getTime())
            );
        } else {
            f = String.format(
                    getString(R.string.activity_sleep_duration__sd_period_text_format),
                    sdf1.format(begin.getTime()),
                    sdf1.format(end.getTime())
            );
        }
        TV_SD_Period.setText(f);
        try {
            loadSD(SD_PERIOD, SD_INDEX);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_duration);

        ImageView IV_Btn__Back = findViewById(R.id.SleepDurationActivity__Back_Btn);
        IV_Btn__Back.setOnClickListener(v -> finish());

        ImageView IV_Btn__Add = findViewById(R.id.SleepDurationActivity__Add_Btn);
        IV_Btn__Add.setOnClickListener(v -> {
            Intent intent = new Intent(SleepDurationActivity.this, SetSleepDurationActivity.class);
            startActivity(intent);
        });

        ImageView IV_Btn__Pre = findViewById(R.id.SleepDurationActivity__Pre_Btn);
        IV_Btn__Pre.setOnClickListener(v -> {
            SD_INDEX++;
            refreshUI();
        });

        ImageView IV_Btn__Next = findViewById(R.id.SleepDurationActivity__Next_Btn);
        IV_Btn__Next.setOnClickListener(v -> {
            SD_INDEX--;
            refreshUI();
        });

        refreshUI();

    }

}