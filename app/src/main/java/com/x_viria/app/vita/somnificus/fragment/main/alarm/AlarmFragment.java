package com.x_viria.app.vita.somnificus.fragment.main.alarm;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetAlarmActivity;
import com.x_viria.app.vita.somnificus.core.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.AlarmTime;
import com.x_viria.app.vita.somnificus.util.Unit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlarmFragment extends Fragment {

    private AlarmViewModel mViewModel;
    private View ROOT;

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    private LinearLayout createAlarmView(JSONObject object) throws JSONException, IOException {
        int iconTintColor = ContextCompat.getColor(requireContext(), R.color.primaryTextColor);

        AlarmSchedule alarmSchedule = new AlarmSchedule(getContext());
        JSONArray time = object.getJSONArray("time");
        int id = object.getInt("schedule_id");

        TypedValue rippleEffect = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        LinearLayout parent = new LinearLayout(getContext());
        parent.setBackgroundResource(rippleEffect.resourceId);
        parent.setClickable(true);
        parent.setGravity(Gravity.CENTER_VERTICAL);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        parent.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetAlarmActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setPadding(
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8)
        );

        SwitchMaterial sw = new SwitchMaterial(requireContext());
        if (object.getBoolean("enable")) sw.setChecked(true);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                AlarmInfo alarmInfo = new AlarmInfo(
                        new AlarmTime(time.getInt(0), time.getInt(1), time.getInt(2)),
                        object.getInt("week"),
                        isChecked
                );
                alarmInfo.setLabel(object.getString("label"));
                alarmSchedule.setSchedule(alarmInfo, id);
                alarmSchedule.sync();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        sw.setPadding(
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8)
        );
        parent.addView(sw);

        LinearLayout mainView = new LinearLayout(getContext());
        mainView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainView.setOrientation(LinearLayout.VERTICAL);
        mainView.setPadding(
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8)
        );

        if (!object.getString("label").equals("")) {
            LinearLayout labelView = new LinearLayout(getContext());
            labelView.setGravity(Gravity.CENTER_VERTICAL);
            labelView.setOrientation(LinearLayout.HORIZONTAL);
            ViewGroup.LayoutParams labelIconLP = new LinearLayout.LayoutParams(
                    Unit.dp2px(requireContext(), 10),
                    Unit.dp2px(requireContext(), 10)
            );
            ViewGroup.MarginLayoutParams labelIconMLP = (ViewGroup.MarginLayoutParams) labelIconLP;
            labelIconMLP.setMargins(0, 0, Unit.dp2px(requireContext(), 4), 0);
            ImageView labelIcon = new ImageView(getContext());
            labelIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_label));
            labelIcon.setImageTintList(ColorStateList.valueOf(iconTintColor));
            labelIcon.setLayoutParams(labelIconMLP);
            labelView.addView(labelIcon);
            TextView labelText = new TextView(getContext());
            labelText.setText(object.getString("label"));
            labelText.setTextSize(10);
            labelView.addView(labelText);
            mainView.addView(labelView);
        }

        TextView timeView = new TextView(getContext());
        timeView.setText(String.format("%02d:%02d", time.getInt(0), time.getInt(1)));
        timeView.setTextSize(28);
        mainView.addView(timeView);

        AlarmInfo alarmInfo = new AlarmInfo(
                new AlarmTime(time.getInt(0), time.getInt(1), time.getInt(2)),
                object.getInt("week"),
                true
        );
        String targetDay = alarmSchedule.getNextDate(alarmInfo);

        TextView dayView = new TextView(getContext());
        dayView.setText(targetDay);
        mainView.addView(dayView);

        parent.addView(mainView);

        LinearLayout rightView = new LinearLayout(getContext());
        rightView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        rightView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView deleteBtn = new ImageView(getContext());
        deleteBtn.setBackgroundResource(rippleEffect.resourceId);
        deleteBtn.setClickable(true);
        deleteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
        deleteBtn.setImageTintList(ColorStateList.valueOf(iconTintColor));
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                Unit.dp2px(requireContext(), 48),
                Unit.dp2px(requireContext(), 48)
        ));
        deleteBtn.setOnClickListener(v -> {
            try {
                if (alarmSchedule.removeSchedule(id)) {
                    refreshAlarmList();
                } else {
                    Toast.makeText(getContext(), getString(R.string.fragment_main_alarm__msg_failed_to_del_alarm), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        deleteBtn.setPadding(
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8),
                Unit.dp2px(requireContext(), 8)
        );
        rightView.addView(deleteBtn);

        parent.addView(rightView);

        return parent;
    }

    private void refreshAlarmList() {
        LinearLayout scheduleView = ROOT.findViewById(R.id.AlarmFragment__Schedule_View);
        scheduleView.removeAllViews();
        try {
            List<JSONObject> alarmList = new ArrayList<>();
            AlarmSchedule alarmSchedule = new AlarmSchedule(getContext());

            LinearLayout noAlarmView = ROOT.findViewById(R.id.AlarmFragment__NoAlarmSet);
            ScrollView scrollView = ROOT.findViewById(R.id.AlarmFragment__ScrollView);
            if (alarmSchedule.getScheduledNum() == 0) {
                noAlarmView.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
            } else {
                noAlarmView.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            JSONArray alarm = alarmSchedule.getSchedule();
            for (int i = 0; i < alarm.length(); i++) {
                alarmList.add(alarm.getJSONObject(i));
            }
            alarmList.sort((o1, o2) -> {
                try {
                    JSONArray t1 = o1.getJSONArray("time");
                    JSONArray t2 = o2.getJSONArray("time");
                    if (t1.getInt(0) == t2.getInt(0)) {
                        return t1.getInt(1) - t2.getInt(1);
                    } else {
                        return t1.getInt(0) - t2.getInt(0);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            for (int j = 0; j < alarmList.size(); j++) {
                JSONObject object = alarmList.get(j);
                LinearLayout view = createAlarmView(object);
                scheduleView.addView(view);
            }
            alarmSchedule.sync();
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_alarm, container, false);

        FloatingActionButton fab = ROOT.findViewById(R.id.AlarmFragment__FloatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetAlarmActivity.class);
            intent.putExtra("id", -1);
            startActivity(intent);
        });

        Log.d("AlarmFragment", "onCreateView()");

        refreshAlarmList();

        return ROOT;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAlarmList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        // TODO: Use the ViewModel
    }

}