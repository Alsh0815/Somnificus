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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.service.TimerService;
import com.x_viria.app.vita.somnificus.util.storage.SPKey;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFragment extends Fragment {

    private TimerViewModel mViewModel;

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    private Handler HANDLE;
    private Intent TIMER_INTENT;
    private List<Integer> TIMER_DISP;
    private Runnable RUNNABLE;

    private boolean isRunning = false;

    private boolean isRunning() {
        ActivityManager manager = (ActivityManager) requireActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TimerService.class.getName().equals(serviceInfo.service.getClassName())) {
                isRunning = true;
                return true;
            }
        }
        isRunning = false;
        return false;
    }

    private void updateCP(View root) {
        LinearLayout NumPanel = root.findViewById(R.id.TimerFragment__NumPanel);
        LinearLayout CP_StartPause = root.findViewById(R.id.TimerFragment__CP_Btn_StartPause);
        ImageView CP_StartPause_Icon = root.findViewById(R.id.TimerFragment__CP_Btn_Start_Icon);
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
                        updateTimer(root);
                        if (isRunning) HANDLE.postDelayed(this, 100);
                    }
                };
                HANDLE = new Handler();
                HANDLE.postDelayed(RUNNABLE, 100);
                NumPanel.setVisibility(View.INVISIBLE);
                updateCP(root);
            });
            CP_StartPause_Icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_start));
        } else {
            CP_StartPause.setOnClickListener(v -> {
                requireActivity().stopService(TIMER_INTENT);
                long target_ms = (new SPStorage(requireContext()).getLong(SPKey.TMP__TIMER_VAL, 0));
                long now_ms = System.currentTimeMillis();
                long rem_ms = (target_ms - now_ms) > 0 ? target_ms - now_ms : 0;
                long sec = (rem_ms / 1000) % 60;
                long min = (rem_ms / (1000 * 60)) % 60;
                long hour = (rem_ms / (1000 * 60 * 60)) % 24;
                for (int i = 0; i < 6; i++) removeNum(root);
                addNum((int) hour / 10, root);
                addNum((int) hour % 10, root);
                addNum((int) min / 10, root);
                addNum((int) min % 10, root);
                addNum((int) sec / 10, root);
                addNum((int) sec % 10, root);
                NumPanel.setVisibility(View.VISIBLE);
                updateCP(root);
            });
            CP_StartPause_Icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_timer_pause));
        }
    }

    private void updateTimer(View root) {
        int[] disp = new int[6];
        for (int i = 0; i < TIMER_DISP.size(); i++) {
            disp[6 - TIMER_DISP.size() + i] = TIMER_DISP.get(i);
        }

        LinearLayout start_btn = root.findViewById(R.id.TimerFragment__CP_Btn_StartPause);
        if (TIMER_DISP.size() == 0) {
            start_btn.setVisibility(View.INVISIBLE);
        } else {
            start_btn.setVisibility(View.VISIBLE);
        }

        TextView timer_1 = root.findViewById(R.id.TimerFragment__Timer_1);
        TextView timer_h = root.findViewById(R.id.TimerFragment__Timer_H);
        TextView timer_2 = root.findViewById(R.id.TimerFragment__Timer_2);
        TextView timer_m = root.findViewById(R.id.TimerFragment__Timer_M);
        TextView timer_3 = root.findViewById(R.id.TimerFragment__Timer_3);
        TextView timer_s = root.findViewById(R.id.TimerFragment__Timer_S);

        timer_1.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        timer_h.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        timer_2.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        timer_m.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        timer_3.setTextColor(getResources().getColor(R.color.secondaryTextColor));
        timer_s.setTextColor(getResources().getColor(R.color.secondaryTextColor));

        if (!isRunning()) {
            timer_1.setText(String.format("%d%d", disp[0], disp[1]));
            timer_2.setText(String.format("%d%d", disp[2], disp[3]));
            timer_3.setText(String.format("%d%d", disp[4], disp[5]));

            if (TIMER_DISP.size() > 0) {
                timer_3.setTextColor(getResources().getColor(R.color.primaryActiveColor));
                timer_s.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            } if (TIMER_DISP.size() > 2) {
                timer_2.setTextColor(getResources().getColor(R.color.primaryActiveColor));
                timer_m.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            } if (TIMER_DISP.size() > 4) {
                timer_1.setTextColor(getResources().getColor(R.color.primaryActiveColor));
                timer_h.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            }
        } else {
            long target_ms = (new SPStorage(requireContext()).getLong(SPKey.TMP__TIMER_VAL, 0));
            long now_ms = System.currentTimeMillis();
            long rem_ms = (target_ms - now_ms) > 0 ? target_ms - now_ms : 0;
            long sec = (rem_ms / 1000) % 60;
            long min = (rem_ms / (1000 * 60)) % 60;
            long hour = (rem_ms / (1000 * 60 * 60)) % 24;
            timer_1.setText(String.format("%02d", hour));
            timer_2.setText(String.format("%02d", min));
            timer_3.setText(String.format("%02d", sec));
            timer_3.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            timer_s.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            timer_2.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            timer_m.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            timer_1.setTextColor(getResources().getColor(R.color.primaryActiveColor));
            timer_h.setTextColor(getResources().getColor(R.color.primaryActiveColor));
        }
    }

    private void addNum(int num, View root) {
        if (TIMER_DISP.size() == 0 && num == 0) {
            return;
        } else if (TIMER_DISP.size() == 6) {
            return;
        }
        TIMER_DISP.add(num);
        updateTimer(root);
    }

    private void removeNum(View root) {
        if (TIMER_DISP.size() == 0) return;
        TIMER_DISP.remove(TIMER_DISP.size() - 1);
        updateTimer(root);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_timer, container, false);

        TIMER_DISP = new ArrayList<>();

        updateTimer(root);

        LinearLayout Num_1 = root.findViewById(R.id.TimerFragment__Num_1);
        LinearLayout Num_2 = root.findViewById(R.id.TimerFragment__Num_2);
        LinearLayout Num_3 = root.findViewById(R.id.TimerFragment__Num_3);
        LinearLayout Num_4 = root.findViewById(R.id.TimerFragment__Num_4);
        LinearLayout Num_5 = root.findViewById(R.id.TimerFragment__Num_5);
        LinearLayout Num_6 = root.findViewById(R.id.TimerFragment__Num_6);
        LinearLayout Num_7 = root.findViewById(R.id.TimerFragment__Num_7);
        LinearLayout Num_8 = root.findViewById(R.id.TimerFragment__Num_8);
        LinearLayout Num_9 = root.findViewById(R.id.TimerFragment__Num_9);
        LinearLayout Num_0 = root.findViewById(R.id.TimerFragment__Num_0);
        LinearLayout Num_00 = root.findViewById(R.id.TimerFragment__Num_00);
        LinearLayout Num_BS = root.findViewById(R.id.TimerFragment__Num_BS);
        LinearLayout CP_StartPause = root.findViewById(R.id.TimerFragment__CP_Btn_StartPause);

        Num_1.setOnClickListener(v -> addNum(1, root));
        Num_2.setOnClickListener(v -> addNum(2, root));
        Num_3.setOnClickListener(v -> addNum(3, root));
        Num_4.setOnClickListener(v -> addNum(4, root));
        Num_5.setOnClickListener(v -> addNum(5, root));
        Num_6.setOnClickListener(v -> addNum(6, root));
        Num_7.setOnClickListener(v -> addNum(7, root));
        Num_8.setOnClickListener(v -> addNum(8, root));
        Num_9.setOnClickListener(v -> addNum(9, root));
        Num_0.setOnClickListener(v -> addNum(0, root));
        Num_00.setOnClickListener(v -> {
            addNum(0, root);
            addNum(0, root);
        });
        Num_BS.setOnClickListener(v -> removeNum(root));

        updateCP(root);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // TODO: Use the ViewModel
    }

}