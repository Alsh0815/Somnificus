package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.AlarmTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SetAlarmActivity extends AppCompatActivity {

    private final boolean[] OPTION__D_OF_W = {false, false, false, false, false, false, false};
    private final int[] VAL__D_OF_W = {AlarmInfo.WEEK__SUN, AlarmInfo.WEEK__MON, AlarmInfo.WEEK__TUE, AlarmInfo.WEEK__WED, AlarmInfo.WEEK__THU, AlarmInfo.WEEK__FRI, AlarmInfo.WEEK__SAT};

    private void refreshDofWView() {
        LinearLayout check_DofW_Sun = findViewById(R.id.SetAlarmActivity__Check_DofW_Sun);
        TextView textView1 = findViewById(R.id.SetAlarmActivity__Check_DofW_Sun_Text);
        if (OPTION__D_OF_W[0]) {
            check_DofW_Sun.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView1.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Sun.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView1.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Mon = findViewById(R.id.SetAlarmActivity__Check_DofW_Mon);
        TextView textView2 = findViewById(R.id.SetAlarmActivity__Check_DofW_Mon_Text);
        if (OPTION__D_OF_W[1]) {
            check_DofW_Mon.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView2.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Mon.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView2.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Tue = findViewById(R.id.SetAlarmActivity__Check_DofW_Tue);
        TextView textView3 = findViewById(R.id.SetAlarmActivity__Check_DofW_Tue_Text);
        if (OPTION__D_OF_W[2]) {
            check_DofW_Tue.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView3.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Tue.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView3.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Wed = findViewById(R.id.SetAlarmActivity__Check_DofW_Wed);
        TextView textView4 = findViewById(R.id.SetAlarmActivity__Check_DofW_Wed_Text);
        if (OPTION__D_OF_W[3]) {
            check_DofW_Wed.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView4.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Wed.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView4.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Thu = findViewById(R.id.SetAlarmActivity__Check_DofW_Thu);
        TextView textView5 = findViewById(R.id.SetAlarmActivity__Check_DofW_Thu_Text);
        if (OPTION__D_OF_W[4]) {
            check_DofW_Thu.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView5.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Thu.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView5.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Fri = findViewById(R.id.SetAlarmActivity__Check_DofW_Fri);
        TextView textView6 = findViewById(R.id.SetAlarmActivity__Check_DofW_Fri_Text);
        if (OPTION__D_OF_W[5]) {
            check_DofW_Fri.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView6.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Fri.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView6.setTextColor(getColor(R.color.primaryTextColor));
        }
        LinearLayout check_DofW_Sat = findViewById(R.id.SetAlarmActivity__Check_DofW_Sat);
        TextView textView7 = findViewById(R.id.SetAlarmActivity__Check_DofW_Sat_Text);
        if (OPTION__D_OF_W[6]) {
            check_DofW_Sat.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week_selected));
            textView7.setTextColor(getColor(R.color.secondaryBackgroundColor));
        } else {
            check_DofW_Sat.setBackground(getDrawable(R.drawable.bg_set_alarm_day_of_week));
            textView7.setTextColor(getColor(R.color.primaryTextColor));
        }

        refreshNearest();
    }

    private void refreshNearest() {
        NumberPicker np_h = findViewById(R.id.SetAlarmActivity__NumPicker_H);
        NumberPicker np_m = findViewById(R.id.SetAlarmActivity__NumPicker_M);
        int week = 0;
        for (int i = 0; i < OPTION__D_OF_W.length; i++) {
            if (OPTION__D_OF_W[i]) week = week | VAL__D_OF_W[i];
        }
        if (week == 0) week = AlarmInfo.WEEK__ALL;
        AlarmInfo alarmInfo = new AlarmInfo(
                new AlarmTime(np_h.getValue(), np_m.getValue(), 0),
                week,
                true
        );
        try {
            AlarmSchedule alarmSchedule = new AlarmSchedule(this);
            TextView tv_nearest = findViewById(R.id.SetAlarmActivity__TextView_Nearest_Alarm);
            tv_nearest.setText(alarmSchedule.getNextDate(alarmInfo));
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        int id = getIntent().getIntExtra("id", -1);
        Toast.makeText(this, "Schedule ID: " + id, Toast.LENGTH_SHORT).show();

        ImageView back_btn = findViewById(R.id.SetAlarmActivity__Btn_Back);
        back_btn.setOnClickListener(v -> finish());

        EditText et_label = findViewById(R.id.SetAlarmActivity__EditText_Label);

        NumberPicker np_h = findViewById(R.id.SetAlarmActivity__NumPicker_H);
        np_h.setMaxValue(23);
        np_h.setMinValue(0);
        np_h.setValue(8);
        np_h.setOnScrollListener((view, scrollState) -> refreshNearest());
        NumberPicker np_m = findViewById(R.id.SetAlarmActivity__NumPicker_M);
        np_m.setMaxValue(59);
        np_m.setMinValue(0);
        np_m.setValue(0);
        np_m.setOnScrollListener((view, scrollState) -> refreshNearest());

        boolean enable = true;

        if (id != -1) {
            try {
                AlarmSchedule alarmSchedule = new AlarmSchedule(this);
                JSONObject object = alarmSchedule.getSchedule(id);
                JSONArray time = object.getJSONArray("time");
                et_label.setText(object.getString("label"));
                np_h.setValue(time.getInt(0));
                np_m.setValue(time.getInt(1));
                enable = object.getBoolean("enable");
                int week = object.getInt("week");

                for (int i = 0; i < OPTION__D_OF_W.length; i++) {
                    if ((week & VAL__D_OF_W[i]) == VAL__D_OF_W[i]) OPTION__D_OF_W[i] = true;
                }
                refreshDofWView();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        TextView save_btn = findViewById(R.id.SetAlarmActivity__Btn_Save);
        boolean finalEnable = enable;
        save_btn.setOnClickListener(v -> {
            int week = 0;
            for (int i = 0; i < OPTION__D_OF_W.length; i++) {
                if (OPTION__D_OF_W[i]) week = week | VAL__D_OF_W[i];
            }
            if (week == 0) week = AlarmInfo.WEEK__ALL;
            AlarmInfo alarmInfo = new AlarmInfo(
                    new AlarmTime(np_h.getValue(), np_m.getValue(), 0),
                    week,
                    finalEnable
            );
            alarmInfo.setLabel(et_label.getText().toString());
            try {
                AlarmSchedule alarmSchedule = new AlarmSchedule(this);
                if (id != -1) {
                    alarmSchedule.setSchedule(alarmInfo, id);
                } else {
                    alarmSchedule.setSchedule(alarmInfo);
                }
                alarmSchedule.sync();
                finish();
            } catch (JSONException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        LinearLayout check_DofW_Sun = findViewById(R.id.SetAlarmActivity__Check_DofW_Sun);
        check_DofW_Sun.setOnClickListener(v -> {
            OPTION__D_OF_W[0] = !OPTION__D_OF_W[0];
            refreshDofWView();
        });
        LinearLayout check_DofW_Mon = findViewById(R.id.SetAlarmActivity__Check_DofW_Mon);
        check_DofW_Mon.setOnClickListener(v -> {
            OPTION__D_OF_W[1] = !OPTION__D_OF_W[1];
            refreshDofWView();
        });
        LinearLayout check_DofW_Tue = findViewById(R.id.SetAlarmActivity__Check_DofW_Tue);
        check_DofW_Tue.setOnClickListener(v -> {
            OPTION__D_OF_W[2] = !OPTION__D_OF_W[2];
            refreshDofWView();
        });
        LinearLayout check_DofW_Wed = findViewById(R.id.SetAlarmActivity__Check_DofW_Wed);
        check_DofW_Wed.setOnClickListener(v -> {
            OPTION__D_OF_W[3] = !OPTION__D_OF_W[3];
            refreshDofWView();
        });
        LinearLayout check_DofW_Thu = findViewById(R.id.SetAlarmActivity__Check_DofW_Thu);
        check_DofW_Thu.setOnClickListener(v -> {
            OPTION__D_OF_W[4] = !OPTION__D_OF_W[4];
            refreshDofWView();
        });
        LinearLayout check_DofW_Fri = findViewById(R.id.SetAlarmActivity__Check_DofW_Fri);
        check_DofW_Fri.setOnClickListener(v -> {
            OPTION__D_OF_W[5] = !OPTION__D_OF_W[5];
            refreshDofWView();
        });
        LinearLayout check_DofW_Sat = findViewById(R.id.SetAlarmActivity__Check_DofW_Sat);
        check_DofW_Sat.setOnClickListener(v -> {
            OPTION__D_OF_W[6] = !OPTION__D_OF_W[6];
            refreshDofWView();
        });
    }

}