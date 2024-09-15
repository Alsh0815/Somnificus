package com.x_viria.app.vita.somnificus.activity.sda;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.core.ui.overlay.PopupTip;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SleepDurationStatisticsActivity extends AppCompatActivity {

    private static final long CONST__MIN_SLEEP_TIME_REQ = 5 * 60 * 60 * 1000;

    private void process() throws JSONException {
        SleepDurationManager sdm = new SleepDurationManager(this);
        List<SleepDurationInfo> sdlist = sdm.get(SleepDurationManager.Period.LAST_30DAYS);

        int pre_day = 0;
        List<Long> stime_day = new ArrayList<>();
        List<Integer> eval_day = new ArrayList<>();
        int days = 0;
        for (SleepDurationInfo sdinfo : sdlist) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(sdinfo.getWakeupTime());

            if (cal.get(Calendar.DAY_OF_YEAR) != pre_day) {
                pre_day = cal.get(Calendar.DAY_OF_YEAR);
                stime_day.add(sdinfo.getWakeupTime() - sdinfo.getBedTime());
                eval_day.add(sdinfo.EVAL__GOOD_OR_BAD);
                days++;
            } else {
                stime_day.set(days - 1, stime_day.get(days - 1) + (sdinfo.getWakeupTime() - sdinfo.getBedTime()));
                if (eval_day.get(days - 1) != SleepDurationInfo.Eval.GOOD) {
                    eval_day.set(days - 1, sdinfo.EVAL__GOOD_OR_BAD);
                }
            }
        }

        if (stime_day.size() < 7) {
            findViewById(R.id.SleepDurationStatisticsActivity__ERR).setVisibility(View.VISIBLE);
            findViewById(R.id.SleepDurationStatisticsActivity__Stats).setVisibility(View.GONE);
            return;
        }

        long totalTime = 0;
        long totalTime_all = 0;
        long minTime = Long.MAX_VALUE;
        int e_days = 0;
        for (int i = 0; i < stime_day.size(); i++) {
            totalTime_all += stime_day.get(i);
            if (eval_day.get(i) == SleepDurationInfo.Eval.GOOD) {
                if (stime_day.get(i) < minTime && CONST__MIN_SLEEP_TIME_REQ <= stime_day.get(i)) minTime = stime_day.get(i);
                totalTime += stime_day.get(i);
                e_days++;
            }
        }

        long avgTime = totalTime / Math.max(e_days, 1);
        avgTime = Math.max(avgTime, minTime);
        long minutes = avgTime / (1000 * 60);
        long roundedMinutes = ((minutes + 9) / 10) * 10;
        long hours = roundedMinutes / 60;
        long remainingMinutes = roundedMinutes % 60;

        ((ProgressBar) findViewById(R.id.SleepDurationStatisticsActivity__Stat_OST_Progress)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__Stat_OST_Text)).setText(
                String.format(getString(R.string.activity_sleep_duration_statistics__text_ost_format), hours, remainingMinutes)
        );

        long m_min = minTime / (1000 * 60);
        long m_roundedMinutes = ((m_min + 9) / 10) * 10;
        long m_hour = m_roundedMinutes / 60;
        long m_remainingMinutes = m_roundedMinutes % 60;

        ((ProgressBar) findViewById(R.id.SleepDurationStatisticsActivity__Stat_MSTR_Progress)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__Stat_MSTR_Text)).setText(
                String.format(getString(R.string.activity_sleep_duration_statistics__text_mstr_format), m_hour, m_remainingMinutes)
        );

        long a_time = totalTime_all / Math.max(days, 1);
        long a_sec = a_time / 1000;
        long a_hour = a_sec / 3600;
        long a_min = (a_sec % 3600) / 60;

        ((ProgressBar) findViewById(R.id.SleepDurationStatisticsActivity__Stat_AST_Progress)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__Stat_AST_Text)).setText(
                String.format(getString(R.string.activity_sleep_duration_statistics__text_ast_format), a_hour, a_min)
        );

        double base_score = 100.0;
        double score = base_score * (minTime <= a_time ? 1.8 : 1.1);
        score += (double)a_time / 1000 / 60;
        score *= avgTime <= a_time ? 2.4 : 1.1;
        score *= (double)e_days / (double)days * 10.5;
        score = Math.max(score, 0);

        ((ProgressBar) findViewById(R.id.SleepDurationStatisticsActivity__Stat_SR_Progress)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__Stat_SR_Text)).setText(
                String.format(getString(R.string.activity_sleep_duration_statistics__text_sr_format), (long)score)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_duration_statistics);

        ImageView IV_Btn__Back = findViewById(R.id.SleepDurationStatisticsActivity__Back_Btn);
        IV_Btn__Back.setOnClickListener(v -> finish());

        ImageView IV_Info__SR = findViewById(R.id.SleepDurationStatisticsActivity__Stat_SR_Info);
        IV_Info__SR.setOnClickListener(v -> {
            new PopupTip(this).show(v, R.string.activity_sleep_duration_statistics__tip_sr);
        });

        new Handler().post(() -> {
            try {
                process();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

}