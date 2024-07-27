package com.x_viria.app.vita.somnificus.fragment.main.sleep;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetSleepDurationActivity;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.ui.StatsGraphView;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class SleepFragment extends Fragment {

    private SleepViewModel mViewModel;

    private View ROOT;

    private long SET_WAKEUP_TIME = -1;

    public static SleepFragment newInstance() {
        return new SleepFragment();
    }

    private void refreshUI() {
        StatsGraphView statsGraphView = ROOT.findViewById(R.id.SleepFragment__StatsGraph);
        statsGraphView.setVisibility(View.VISIBLE);

        LinearLayout LL_BedTime = ROOT.findViewById(R.id.SleepFragment__Bed_Time);
        LL_BedTime.setOnClickListener(v -> {
            // P
        });

        LinearLayout LL_WakeUpTime = ROOT.findViewById(R.id.SleepFragment__WakeUp_Time);
        LL_WakeUpTime.setOnClickListener(v -> {
            if (SET_WAKEUP_TIME == -1) {
                Toast.makeText(getContext(), getString(R.string.fragment_main_sleep__toast_no_alarm_24hours), Toast.LENGTH_SHORT).show();
            } else {
                Date d = new Date();
                d.setTime(SET_WAKEUP_TIME);
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                Toast.makeText(
                        getContext(),
                        String.format(getString(R.string.fragment_main_sleep__toast_wakeup_time), f.format(d)),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        try {
            AlarmSchedule alarmSchedule = new AlarmSchedule(requireContext());
            AlarmInfo nextAlarm = alarmSchedule.getNextAlarm(true);
            if (nextAlarm != null) {
                if (nextAlarm.getNextTime() - System.currentTimeMillis() < 24 * 60 * 60 * 1000) {
                    Date d = new Date();
                    d.setTime(nextAlarm.getNextTime());
                    SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                    TextView T1 = ROOT.findViewById(R.id.SleepFragment__Schedule_WakeUp_Time_T1);
                    T1.setTextColor(getResources().getColor(R.color.white));
                    TextView T2 = ROOT.findViewById(R.id.SleepFragment__Schedule_WakeUp_Time_T2);
                    T2.setText(f.format(d));
                    T2.setTextColor(getResources().getColor(R.color.white));
                    SET_WAKEUP_TIME = nextAlarm.getNextTime();
                }
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_sleep, container, false);

        ImageView IVBtn_Add_SD = ROOT.findViewById(R.id.SleepFragment__SD_Add);
        IVBtn_Add_SD.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetSleepDurationActivity.class);
            startActivity(intent);
        });

        refreshUI();

        return ROOT;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshUI();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SleepViewModel.class);
    }

}