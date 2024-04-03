package com.x_viria.app.vita.somnificus.fragment.stopwatch;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;

import java.util.Timer;
import java.util.TimerTask;

public class StopwatchFragment extends Fragment {

    private StopwatchViewModel mViewModel;

    public static StopwatchFragment newInstance() {
        return new StopwatchFragment();
    }

    private static Timer TIMER;
    private static TimerTask TIMER_TASK;
    private static Runnable RUNNABLE;
    private static Handler HANDLE;

    private boolean isRunning = false;
    private long beginMS = 0;
    private long elapsedMS = 0;
    private long nowMS = 0;

    private void updateWatch(View root) {
        long ms = (nowMS - beginMS) % 1000 / 10;
        int rsec = (int) (nowMS - beginMS) / 1000;
        int sec = rsec % 60;
        int min = (rsec % 3600) / 60;
        int hour = rsec / 3600;
        TextView tshow_ms = root.findViewById(R.id.StopwatchFragment__TShow_MS);
        TextView tshow_s = root.findViewById(R.id.StopwatchFragment__TShow_S);
        tshow_ms.setText(String.format("%02d", ms));
        if (min == 0 && hour == 0) {
            tshow_s.setText(String.format("%02d", sec));
        } else if (min != 0 && hour == 0) {
            tshow_s.setText(String.format("%d:%02d", min, sec));
        } else {
            tshow_s.setText(String.format("%d:%02d:%02d", hour, min, sec));
        }
    }

    private void startWatch(View root) {
        ImageView icon = root.findViewById(R.id.StopwatchFragment__CP_Btn_StartPause_Icon);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_pause));
        LinearLayout resetBtn = root.findViewById(R.id.StopwatchFragment__CP_Btn_Reset);
        resetBtn.setVisibility(View.VISIBLE);
        LinearLayout lapBtn = root.findViewById(R.id.StopwatchFragment__CP_Btn_Lap);
        lapBtn.setVisibility(View.VISIBLE);
        beginMS = System.currentTimeMillis() - elapsedMS;
        RUNNABLE = new Runnable() {
            @Override
            public void run() {
                nowMS = System.currentTimeMillis();
                updateWatch(root);
                HANDLE.postDelayed(this, 10);
            }
        };
        HANDLE = new Handler();
        HANDLE.post(RUNNABLE);
        isRunning = true;
    }

    private void pauseWatch(View root) {
        elapsedMS = nowMS - beginMS;
        nowMS = 0;
        beginMS = 0;
        ImageView icon = root.findViewById(R.id.StopwatchFragment__CP_Btn_StartPause_Icon);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_start));
        LinearLayout lapBtn = root.findViewById(R.id.StopwatchFragment__CP_Btn_Lap);
        lapBtn.setVisibility(View.INVISIBLE);
        HANDLE.removeCallbacks(RUNNABLE);
        isRunning = false;
    }

    private void resetWatch(View root) {
        pauseWatch(root);
        LinearLayout resetBtn = root.findViewById(R.id.StopwatchFragment__CP_Btn_Reset);
        resetBtn.setVisibility(View.INVISIBLE);
        beginMS = 0;
        elapsedMS = 0;
        nowMS = 0;
        updateWatch(root);
    }

    private void toggle(View root) {
        if (!isRunning) {
            startWatch(root);
        } else {
            pauseWatch(root);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_stopwatch, container, false);

        LinearLayout CP_StartPause_Btn = root.findViewById(R.id.StopwatchFragment__CP_Btn_StartPause);
        CP_StartPause_Btn.setOnClickListener(v -> {
            toggle(root);
        });

        LinearLayout CP_Reset_Btn = root.findViewById(R.id.StopwatchFragment__CP_Btn_Reset);
        CP_Reset_Btn.setOnClickListener(v -> resetWatch(root));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StopwatchViewModel.class);
        // TODO: Use the ViewModel
    }

}