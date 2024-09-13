package com.x_viria.app.vita.somnificus.activity.sda;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetSleepDurationActivity;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.util.Unit;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SleepDurationActivity extends AppCompatActivity {

    private int SD_INDEX = 0;
    private int SD_PERIOD = SleepDurationManager.Period.WEEK;

    private List<Long> DAY_OF_SLEEP_TIME;

    private void loadSD(int period, int index) throws JSONException {
        DAY_OF_SLEEP_TIME = new ArrayList<>();
        LinearLayout LL__SDList = findViewById(R.id.SleepDurationActivity__SDList);
        LL__SDList.removeAllViews();

        TypedValue rippleEffect = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        int iconTintColor = ContextCompat.getColor(this, R.color.primaryTextColor);

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
        TextView pre_header = null;
        Date d_to = null;
        for (SleepDurationInfo info : list) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(info.getWakeupTime());

            int padding1 = Unit.dp2px(this, 12.0f);
            int padding2 = Unit.dp2px(this, 12.0f);

            long duration = info.getWakeupTime() - info.getBedTime();
            long sec = duration / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;
            totalTime += duration;

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

                d_to = new Date();
                d_to.setTime(info.getWakeupTime());
                SimpleDateFormat f = new SimpleDateFormat(getString(R.string.activity_sleep_duration__date_format_from));
                pre_header = new TextView(this);
                pre_header.setPadding(padding2, padding2, padding2, padding2);
                pre_header.setText(String.format("%s - " + getString(R.string.activity_sleep_duration__time_format), f.format(d_to), hour, min));
                pre_header.setTextColor(getColor(R.color.secondaryTextColor));
                pre_header.setTextSize(COMPLEX_UNIT_DIP, 14);

                head.addView(icon);
                head.addView(pre_header);

                pre_parent.addView(head);

                DAY_OF_SLEEP_TIME.add(duration);

                cnt_day++;
            } else {
                long day_of_sleep_time = DAY_OF_SLEEP_TIME.get(cnt_day - 1) + duration;
                long t_sec = day_of_sleep_time / 1000;
                long t_hour = t_sec / 3600;
                long t_min = (t_sec % 3600) / 60;
                SimpleDateFormat f = new SimpleDateFormat(getString(R.string.activity_sleep_duration__date_format_from));
                assert pre_header != null;
                pre_header.setText(String.format("%s - " + getString(R.string.activity_sleep_duration__time_format), f.format(d_to), t_hour, t_min));
                DAY_OF_SLEEP_TIME.set(cnt_day - 1, day_of_sleep_time);
            }

            LinearLayout body = new LinearLayout(this);
            body.setBackgroundResource(rippleEffect.resourceId);
            body.setClickable(true);
            body.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            body.setOnClickListener(v -> {
                Intent intent = new Intent(this, SleepDurationDetailsActivity.class);
                intent.putExtra("sdinfo_id", info.ID);
                startActivity(intent);
            });
            body.setOrientation(LinearLayout.HORIZONTAL);

            TextView duration_text = new TextView(this);
            duration_text.setPadding(padding2, padding2, padding2, padding2);
            duration_text.setText(String.format(getString(R.string.fragment_main_sleep__text_sd_time), hour, min));
            duration_text.setTextColor(getColor(R.color.primaryTextColor));
            duration_text.setTextSize(COMPLEX_UNIT_DIP, 18);
            body.addView(duration_text);

            LinearLayout.LayoutParams body_right_lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            body_right_lp.gravity = Gravity.CENTER_VERTICAL;
            LinearLayout body_right = new LinearLayout(this);
            body_right.setGravity(Gravity.END);
            body_right.setLayoutParams(body_right_lp);
            body_right.setOrientation(LinearLayout.HORIZONTAL);
            body.addView(body_right);

            if (duration < 3 * 60 * 60 * 1000 && !info.FLAG__NAP) {
                int iv_padding = Unit.dp2px(this, 4);
                ImageView warning_iv = new ImageView(this);
                warning_iv.setBackgroundResource(rippleEffect.resourceId);
                warning_iv.setClickable(true);
                warning_iv.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_error));
                warning_iv.setImageTintList(ColorStateList.valueOf(iconTintColor));
                warning_iv.setLayoutParams(new LinearLayout.LayoutParams(
                        Unit.dp2px(this, 28),
                        Unit.dp2px(this, 28)
                ));
                warning_iv.setOnClickListener(v -> new AlertDialog.Builder(SleepDurationActivity.this, R.style.SomnificusAlertDialogTheme)
                        .setTitle(R.string.activity_sleep_duration__dialog_advice_title)
                        .setMessage(R.string.activity_sleep_duration__dialog_1_cycle_caution_msg)
                        .setPositiveButton(R.string.common__text_ok, null)
                        .show());
                warning_iv.setPadding(iv_padding, iv_padding, iv_padding, iv_padding);
                body_right.addView(warning_iv);
            }

            pre_parent.addView(body);
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

        long minTime = Long.MAX_VALUE;
        for (long time : DAY_OF_SLEEP_TIME) {
            if (time < minTime) minTime = time;
        }

        LinearLayout AdviceView__ST_Too_Short = findViewById(R.id.SleepDurationActivity__Advice_ST_Too_Short);
        if (minTime < 6 * 60 * 60 * 1000) {
            AdviceView__ST_Too_Short.setVisibility(View.VISIBLE);
        } else {
            AdviceView__ST_Too_Short.setVisibility(View.GONE);
        }

        LinearLayout AdviceView__ST_Avg_Not_App = findViewById(R.id.SleepDurationActivity__Advice_ST_Avg_Not_App);
        if (cnt_day != 0) {
            long avgTime = totalTime / cnt_day;
            if (7 * 60 * 60 * 1000 <= avgTime) {
                AdviceView__ST_Avg_Not_App.setVisibility(View.GONE);
            } else {
                AdviceView__ST_Avg_Not_App.setVisibility(View.VISIBLE);
            }
        } else {
            AdviceView__ST_Avg_Not_App.setVisibility(View.GONE);
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