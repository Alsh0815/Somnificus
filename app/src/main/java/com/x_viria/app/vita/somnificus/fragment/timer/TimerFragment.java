package com.x_viria.app.vita.somnificus.fragment.timer;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.x_viria.app.vita.somnificus.R;

import java.util.ArrayList;
import java.util.List;

public class TimerFragment extends Fragment {

    private TimerViewModel mViewModel;

    public static TimerFragment newInstance() {
        return new TimerFragment();
    }

    private List<Integer> TIMER_DISP;

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

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // TODO: Use the ViewModel
    }

}