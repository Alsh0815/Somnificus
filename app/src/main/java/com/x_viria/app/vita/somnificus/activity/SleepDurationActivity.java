package com.x_viria.app.vita.somnificus.activity;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

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

        long totalTime = 0;

        int cnt_day = 0;
        int pre_day = 0;
        LinearLayout pre_parent = null;
        for (SleepDurationInfo info : list) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(info.getWakeupTime());

            int padding1 = Unit.dp2px(this, 12.0f);
            int padding2 = Unit.dp2px(this, 12.0f);

            if (cal.get(Calendar.DAY_OF_YEAR) != pre_day) {
                pre_day = cal.get(Calendar.DAY_OF_YEAR);
                LinearLayout parent = new LinearLayout(this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.VERTICAL);
                parent.setPadding(padding1, padding1, padding1, padding1);
                LL__SDList.addView(parent);
                pre_parent = parent;

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

                Date d_to = new Date();
                d_to.setTime(info.getWakeupTime());
                SimpleDateFormat f = new SimpleDateFormat(getString(R.string.activity_sleep_duration__date_format_from));
                TextView date = new TextView(this);
                date.setPadding(padding2, padding2, padding2, padding2);
                date.setText(f.format(d_to));
                date.setTextColor(getColor(R.color.secondaryTextColor));
                date.setTextSize(COMPLEX_UNIT_DIP, 14);

                head.addView(icon);
                head.addView(date);

                pre_parent.addView(head);

                cnt_day++;
            }

            long duration = info.getWakeupTime() - info.getBedTime();
            long sec = duration / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;

            totalTime += duration;

            TextView duration_text = new TextView(this);
            duration_text.setBackgroundResource(rippleEffect.resourceId);
            duration_text.setClickable(true);
            duration_text.setPadding(padding2, padding2, padding2, padding2);
            duration_text.setText(String.format(getString(R.string.fragment_main_sleep__text_sd_time), hour, min));
            duration_text.setTextColor(getColor(R.color.primaryTextColor));
            duration_text.setTextSize(COMPLEX_UNIT_DIP, 18);
            duration_text.setOnClickListener(v -> {
                Intent intent = new Intent(this, SleepDurationDetailsActivity.class);
                intent.putExtra("sdinfo_id", info.ID);
                startActivity(intent);
            });

            assert pre_parent != null;
            pre_parent.addView(duration_text);
        }

        TextView tv_period_avg = (TextView) findViewById(R.id.SleepDurationActivity__SD_Avg_Text);
        if (cnt_day == 0) {
            tv_period_avg.setText(getString(R.string.activity_sleep_duration__sd_period_text_avg_zero));
        } else {
            long avgTime = totalTime / cnt_day;
            long sec = avgTime / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;
            tv_period_avg.setText(
                    String.format(
                            getString(R.string.activity_sleep_duration__sd_period_text_avg),
                            hour,
                            min
                    )
            );
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

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }
}