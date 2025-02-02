package com.x_viria.app.vita.somnificus.activity.sda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;
import com.x_viria.app.vita.somnificus.util.Theme;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SleepDurationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Theme.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_duration_details);

        String sdinfo_id = getIntent().getStringExtra("sdinfo_id");

        (findViewById(R.id.SleepDurationDetailsActivity__Back_Btn))
                .setOnClickListener(v -> finish());

        try {
            SleepDurationManager sdm = new SleepDurationManager(this);
            SleepDurationInfo sdinfo = sdm.get(sdinfo_id);
            if (sdinfo == null) return;

            (findViewById(R.id.SleepDurationDetailsActivity__Delete_Btn))
                    .setOnClickListener(v -> new AlertDialog.Builder(SleepDurationDetailsActivity.this, R.style.SomnificusAlertDialogTheme)
                            .setCancelable(false)
                            .setTitle(R.string.activity_sleep_duration_details__dialog_del_sd_title)
                            .setMessage(R.string.activity_sleep_duration_details__dialog_del_sd_msg)
                            .setNegativeButton(R.string.common__text_no, null)
                            .setPositiveButton(R.string.common__text_yes, (dialog, which) -> {
                                try {
                                    sdm.remove(sdinfo_id);
                                    Toast.makeText(SleepDurationDetailsActivity.this, R.string.activity_sleep_duration_details__msg_succeeded_to_del_data, Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    Toast.makeText(SleepDurationDetailsActivity.this, R.string.activity_sleep_duration_details__msg_failed_to_del_data, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show());

            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            long d = sdinfo.getWakeupTime() - sdinfo.getBedTime();
            long sec = d / 1000;
            long hour = sec / 3600;
            long min = (sec % 3600) / 60;

            TextView tv_asleep = findViewById(R.id.SleepDurationDetailsActivity__Asleep_Text);
            if (hour == 0) {
                tv_asleep.setText(
                        String.format(
                                getString(R.string.activity_sleep_duration_details__text_sleep_duration_format_m),
                                min
                        )
                );
            } else if (0 < hour && min == 0) {
                tv_asleep.setText(
                        String.format(
                                getString(R.string.activity_sleep_duration_details__text_sleep_duration_format_h),
                                hour
                        )
                );
            } else if (0 < hour && 0 < min) {
                tv_asleep.setText(
                        String.format(
                                getString(R.string.activity_sleep_duration_details__text_sleep_duration_format_hm),
                                hour,
                                min
                        )
                );
            }

            ((TextView) findViewById(R.id.SleepDurationDetailsActivity__GotInBed_Text))
                    .setText(sdf.format(new Date(sdinfo.getBedTime())));
            ((TextView) findViewById(R.id.SleepDurationDetailsActivity__WokeUp_Text))
                    .setText(sdf.format(new Date(sdinfo.getWakeupTime())));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}