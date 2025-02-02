package com.x_viria.app.vita.somnificus.fragment.main.timer;

import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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
import com.x_viria.app.vita.somnificus.service.TimerService;
import com.x_viria.app.vita.somnificus.util.Theme;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment {

    private TimerViewModel mViewModel;

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    private Handler HANDLE;
    private Intent TIMER_INTENT;
    private List<Integer> TIMER_DISP;
    private View ROOT;
    private Runnable RUNNABLE;

    private boolean isAttached = false;
    private boolean isRunning = false;

    private boolean isRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TimerService.class.getName().equals(serviceInfo.service.getClassName())) {
                TIMER_INTENT = new Intent(getContext(), TimerService.class);
                isRunning = true;
                return true;
            }
        }
        isRunning = false;
        return false;
    }

    private View.OnClickListener onPauseListener() {
        return v -> {
            LinearLayout NumPanel = ROOT.findViewById(R.id.TimerFragment__NumPanel);
            requireActivity().stopService(TIMER_INTENT);
            long target_ms = (new SPStorage(requireContext()).getLong(Config.TMP__TIMER_VAL, 0));
            long now_ms = System.currentTimeMillis();
            long rem_ms = (target_ms - now_ms) > 0 ? target_ms - now_ms : 0;
            long sec = (rem_ms / 1000) % 60;
            long min = (rem_ms / (1000 * 60)) % 60;
            long hour = (rem_ms / (1000 * 60 * 60)) % 24;
            for (int i = 0; i < 6; i++) removeNum();
            addNum((int) hour / 10);
            addNum((int) hour % 10);
            addNum((int) min / 10);
            addNum((int) min % 10);
            addNum((int) sec / 10);
            addNum((int) sec % 10);
            NumPanel.setVisibility(View.VISIBLE);
            updateCP();
        };
    }

    private void updateCP() {
        LinearLayout NumPanel = ROOT.findViewById(R.id.TimerFragment__NumPanel);
        LinearLayout CP_StartPause = ROOT.findViewById(R.id.TimerFragment__CP_Btn_StartPause);
        ImageView CP_StartPause_Icon = ROOT.findViewById(R.id.TimerFragment__CP_Btn_Start_Icon);
        if (!isRunning()) {
            CP_StartPause.setOnClickListener(v -> {
                int[] disp = new int[6];
                for (int i = 0; i < TIMER_DISP.size(); i++) {
                    disp[6 - TIMER_DISP.size() + i] = TIMER_DISP.get(i);
                }
                long time_ms = ((disp[0] * 10 + disp[1]) * 3600 + (disp[2] * 10 + disp[3]) * 60 + (disp[4] * 10 + disp[5])) * 1000;
                TIMER_INTENT = new Intent(getContext(), TimerService.class);
                TIMER_INTENT.putExtra(TimerService.PUT_EXTRA__TIME_MS, time_ms);
                requireActivity().startForegroundService(TIMER_INTENT);
                RUNNABLE = new Runnable() {
                    @Override
                    public void run() {
                        updateTimer();
                        if (isRunning) HANDLE.postDelayed(this, 100);
                    }
                };
                HANDLE = new Handler();
                HANDLE.postDelayed(RUNNABLE, 100);
                NumPanel.setVisibility(View.INVISIBLE);
                updateCP();
            });
            CP_StartPause_Icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_start));
        } else {
            CP_StartPause.setOnClickListener(onPauseListener());
            CP_StartPause_Icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_pause));
        }
    }

    private void updateTimer() {
        if (!isAttached) return;
        int[] disp = new int[6];
        for (int i = 0; i < TIMER_DISP.size(); i++) {
            disp[6 - TIMER_DISP.size() + i] = TIMER_DISP.get(i);
        }

        LinearLayout start_btn = ROOT.findViewById(R.id.TimerFragment__CP_Btn_StartPause);
        if (TIMER_DISP.size() == 0) {
            start_btn.setVisibility(View.INVISIBLE);
        } else {
            start_btn.setVisibility(View.VISIBLE);
        }

        TextView timer_1 = ROOT.findViewById(R.id.TimerFragment__Timer_1);
        TextView timer_h = ROOT.findViewById(R.id.TimerFragment__Timer_H);
        TextView timer_2 = ROOT.findViewById(R.id.TimerFragment__Timer_2);
        TextView timer_m = ROOT.findViewById(R.id.TimerFragment__Timer_M);
        TextView timer_3 = ROOT.findViewById(R.id.TimerFragment__Timer_3);
        TextView timer_s = ROOT.findViewById(R.id.TimerFragment__Timer_S);

        timer_1.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));
        timer_h.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));
        timer_2.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));
        timer_m.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));
        timer_3.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));
        timer_s.setTextColor(Theme.getColor(requireContext(), R.attr.secondaryTextColor));

        if (!isRunning()) {
            timer_1.setText(String.format("%d%d", disp[0], disp[1]));
            timer_2.setText(String.format("%d%d", disp[2], disp[3]));
            timer_3.setText(String.format("%d%d", disp[4], disp[5]));

            if (TIMER_DISP.size() > 0) {
                timer_3.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
                timer_s.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            } if (TIMER_DISP.size() > 2) {
                timer_2.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
                timer_m.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            } if (TIMER_DISP.size() > 4) {
                timer_1.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
                timer_h.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            }
        } else {
            long target_ms = (new SPStorage(requireContext()).getLong(Config.TMP__TIMER_VAL, 0));
            long now_ms = System.currentTimeMillis();
            long rem_ms = (target_ms - now_ms) > 0 ? target_ms - now_ms : 0;
            long sec = (rem_ms / 1000) % 60;
            long min = (rem_ms / (1000 * 60)) % 60;
            long hour = (rem_ms / (1000 * 60 * 60)) % 24;
            timer_1.setText(String.format("%02d", hour));
            timer_2.setText(String.format("%02d", min));
            timer_3.setText(String.format("%02d", sec));
            timer_3.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            timer_s.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            timer_2.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            timer_m.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            timer_1.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
            timer_h.setTextColor(Theme.getColor(requireContext(), R.attr.primaryActiveColor));
        }
    }

    private void addNum(int num) {
        if (TIMER_DISP.size() == 0 && num == 0) {
            return;
        } else if (TIMER_DISP.size() == 6) {
            return;
        }
        TIMER_DISP.add(num);
        updateTimer();
    }

    private void removeNum() {
        if (TIMER_DISP.size() == 0) return;
        TIMER_DISP.remove(TIMER_DISP.size() - 1);
        updateTimer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_timer, container, false);
        isAttached = true;

        TIMER_DISP = new ArrayList<>();

        updateTimer();

        LinearLayout Num_1 = ROOT.findViewById(R.id.TimerFragment__Num_1);
        LinearLayout Num_2 = ROOT.findViewById(R.id.TimerFragment__Num_2);
        LinearLayout Num_3 = ROOT.findViewById(R.id.TimerFragment__Num_3);
        LinearLayout Num_4 = ROOT.findViewById(R.id.TimerFragment__Num_4);
        LinearLayout Num_5 = ROOT.findViewById(R.id.TimerFragment__Num_5);
        LinearLayout Num_6 = ROOT.findViewById(R.id.TimerFragment__Num_6);
        LinearLayout Num_7 = ROOT.findViewById(R.id.TimerFragment__Num_7);
        LinearLayout Num_8 = ROOT.findViewById(R.id.TimerFragment__Num_8);
        LinearLayout Num_9 = ROOT.findViewById(R.id.TimerFragment__Num_9);
        LinearLayout Num_0 = ROOT.findViewById(R.id.TimerFragment__Num_0);
        LinearLayout Num_00 = ROOT.findViewById(R.id.TimerFragment__Num_00);
        LinearLayout Num_BS = ROOT.findViewById(R.id.TimerFragment__Num_BS);

        Num_1.setOnClickListener(v -> addNum(1));
        Num_2.setOnClickListener(v -> addNum(2));
        Num_3.setOnClickListener(v -> addNum(3));
        Num_4.setOnClickListener(v -> addNum(4));
        Num_5.setOnClickListener(v -> addNum(5));
        Num_6.setOnClickListener(v -> addNum(6));
        Num_7.setOnClickListener(v -> addNum(7));
        Num_8.setOnClickListener(v -> addNum(8));
        Num_9.setOnClickListener(v -> addNum(9));
        Num_0.setOnClickListener(v -> addNum(0));
        Num_00.setOnClickListener(v -> {
            addNum(0);
            addNum(0);
        });
        Num_BS.setOnClickListener(v -> removeNum());
        Num_BS.setOnLongClickListener(v -> {
            for (int i = 0; i < 6; i++) removeNum();
            return false;
        });

        return ROOT;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onPause() {
        super.onPause();
        isAttached = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isAttached = true;
        if (!isRunning()) {
            updateCP();
        } else {
            LinearLayout CP_StartPause = ROOT.findViewById(R.id.TimerFragment__CP_Btn_StartPause);
            for (int i = 0; i < 6; i++) addNum(9);
            RUNNABLE = new Runnable() {
                @Override
                public void run() {
                    updateTimer();
                    if (isRunning) HANDLE.postDelayed(this, 100);
                }
            };
            HANDLE = new Handler();
            HANDLE.postDelayed(RUNNABLE, 100);
            LinearLayout NumPanel = ROOT.findViewById(R.id.TimerFragment__NumPanel);
            ImageView CP_StartPause_Icon = ROOT.findViewById(R.id.TimerFragment__CP_Btn_Start_Icon);
            NumPanel.setVisibility(View.INVISIBLE);
            CP_StartPause.setOnClickListener(onPauseListener());
            CP_StartPause_Icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_pause));
            updateCP();
        }
        
    }
}