package com.x_viria.app.vita.somnificus.activity.sda;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SleepDurationStatisticsActivity extends AppCompatActivity {

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

        long totalTime = 0;
        int e_days = 0;
        for (int i = 0; i < stime_day.size(); i++) {
            if (eval_day.get(i) == SleepDurationInfo.Eval.GOOD) {
                totalTime += stime_day.get(i);
                e_days++;
            }
        }

        long avgTime = totalTime / Math.max(e_days, 1);
        long minutes = avgTime / (1000 * 60);
        long roundedMinutes = ((minutes + 9) / 10) * 10;
        long hours = roundedMinutes / 60;
        long remainingMinutes = roundedMinutes % 60;

        ((ProgressBar) findViewById(R.id.SleepDurationStatisticsActivity__Stat_OST_Progress)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.SleepDurationStatisticsActivity__Stat_OST_Text)).setText(
                String.format(getString(R.string.activity_sleep_duration_statistics__text_ost_format), hours, remainingMinutes)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_duration_statistics);

        ImageView IV_Btn__Back = findViewById(R.id.SleepDurationStatisticsActivity__Back_Btn);
        IV_Btn__Back.setOnClickListener(v -> finish());

        new Handler().post(() -> {
            try {
                process();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

}