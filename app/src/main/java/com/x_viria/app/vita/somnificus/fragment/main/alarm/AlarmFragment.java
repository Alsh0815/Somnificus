package com.x_viria.app.vita.somnificus.fragment.main.alarm;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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

import com.android.billingclient.api.BillingClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.activity.SetAlarmActivity;
import com.x_viria.app.vita.somnificus.activity.SetNapActivity;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmInfo;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmSchedule;
import com.x_viria.app.vita.somnificus.core.alarm.AlarmTime;
import com.x_viria.app.vita.somnificus.core.bill.BillingManager;
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

    private boolean IS_FAB_CLICKED = false;

    private boolean IS_PREMIUM = false;
    private BillingManager billingManager;

    public static AlarmFragment newInstance() {
        return new AlarmFragment();
    }

    private LinearLayout createAlarmView(JSONObject object) throws JSONException, IOException {
        int iconTintColor = ContextCompat.getColor(requireContext(), R.color.primaryTextColor);
        int iconTintColorWhite = ContextCompat.getColor(requireContext(), R.color.white);

        JSONObject objdata = object.getJSONObject("data");

        AlarmSchedule alarmSchedule = new AlarmSchedule(getContext());
        JSONArray time = object.getJSONArray("time");
        JSONObject option = objdata.getJSONObject("option");
        boolean enable = objdata.getBoolean("enable");
        int id = object.getInt("schedule_id");

        AlarmInfo alarmInfo = new AlarmInfo(
                new AlarmTime(time.getInt(0), time.getInt(1), time.getInt(2)),
                objdata.getInt("week"),
                objdata.getBoolean("enable")
        );
        if (option.has("mute")) {
            alarmInfo.setOption(AlarmInfo.OPT__MUTE_VOL, option.getBoolean("mute"));
        } else {
            alarmInfo.setOption(AlarmInfo.OPT__MUTE_VOL, false);
        }

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
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );

        SwitchMaterial sw = new SwitchMaterial(requireContext());
        if (objdata.getBoolean("enable")) sw.setChecked(true);
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (alarmSchedule.setEnable(id, isChecked) && isChecked) {
                    alarmInfo.showNextTime(getContext());
                }
                alarmSchedule.sync();
                refreshAlarmList();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
        sw.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );
        parent.addView(sw);

        LinearLayout mainView = new LinearLayout(getContext());
        mainView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mainView.setOrientation(LinearLayout.VERTICAL);
        mainView.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );

        if (!objdata.getString("label").equals("")) {
            LinearLayout labelView = new LinearLayout(getContext());
            labelView.setGravity(Gravity.CENTER_VERTICAL);
            labelView.setOrientation(LinearLayout.HORIZONTAL);
            ViewGroup.MarginLayoutParams labelIconLP = new LinearLayout.LayoutParams(
                    Unit.Pixel.dp2px(requireContext(), 10),
                    Unit.Pixel.dp2px(requireContext(), 10)
            );
            labelIconLP.setMargins(0, 0, Unit.Pixel.dp2px(requireContext(), 4), 0);
            ImageView labelIcon = new ImageView(getContext());
            labelIcon.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_label));
            labelIcon.setImageTintList(ColorStateList.valueOf(iconTintColor));
            if (enable) labelIcon.setImageTintList(ColorStateList.valueOf(iconTintColorWhite));
            labelIcon.setLayoutParams(labelIconLP);
            labelView.addView(labelIcon);
            TextView labelText = new TextView(getContext());
            labelText.setText(objdata.getString("label"));
            if (enable) labelText.setTextColor(requireContext().getColor(R.color.white));
            labelText.setTextSize(10);
            labelView.addView(labelText);
            mainView.addView(labelView);
        }

        TextView timeView = new TextView(getContext());
        timeView.setText(String.format("%02d:%02d", time.getInt(0), time.getInt(1)));
        if (enable) timeView.setTextColor(requireContext().getColor(R.color.white));
        timeView.setTextSize(28);
        mainView.addView(timeView);

        String targetDay = alarmSchedule.getNextDate(alarmInfo);

        TextView dayView = new TextView(getContext());
        dayView.setText(targetDay);
        if (enable) dayView.setTextColor(requireContext().getColor(R.color.white));
        mainView.addView(dayView);

        parent.addView(mainView);

        LinearLayout rightView = new LinearLayout(getContext());
        rightView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        rightView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (alarmInfo.getOption(AlarmInfo.OPT__MUTE_VOL)) {
            ImageView icon_mute = new ImageView(getContext());
            icon_mute.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.ic_volume_off));
            icon_mute.setImageTintList(ColorStateList.valueOf(iconTintColor));
            icon_mute.setLayoutParams(new LinearLayout.LayoutParams(
                    Unit.Pixel.dp2px(requireContext(), 24),
                    Unit.Pixel.dp2px(requireContext(), 24)
            ));
            icon_mute.setPadding(
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2)
            );
            rightView.addView(icon_mute);
        }

        ImageView deleteBtn = new ImageView(getContext());
        deleteBtn.setBackgroundResource(rippleEffect.resourceId);
        deleteBtn.setClickable(true);
        deleteBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_delete));
        deleteBtn.setImageTintList(ColorStateList.valueOf(iconTintColor));
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                Unit.Pixel.dp2px(requireContext(), 48),
                Unit.Pixel.dp2px(requireContext(), 48)
        ));
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext(), R.style.SomnificusAlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.fragment_main_alarm__dialog_del_alarm_title)
                    .setMessage(R.string.fragment_main_alarm__dialog_del_alarm_msg)
                    .setNegativeButton(R.string.common__text_no, null)
                    .setPositiveButton(R.string.common__text_yes, (dialog, which) -> {
                        try {
                            if (alarmSchedule.removeSchedule(id)) {
                                refreshAlarmList();
                            } else {
                                Toast.makeText(getContext(), getString(R.string.fragment_main_alarm__msg_failed_to_del_alarm), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .show();
        });
        deleteBtn.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );
        rightView.addView(deleteBtn);

        parent.addView(rightView);

        return parent;
    }

    private LinearLayout createNapView(JSONObject object) throws JSONException, IOException {
        int iconTintColor = ContextCompat.getColor(requireContext(), R.color.primaryTextColor);
        TypedValue rippleEffect = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, rippleEffect, true);

        JSONObject objdata = object.getJSONObject("data");

        AlarmSchedule alarmSchedule = new AlarmSchedule(getContext());
        JSONArray time = object.getJSONArray("time");
        JSONObject option = objdata.getJSONObject("option");
        int id = object.getInt("schedule_id");

        boolean opt_mute = false;
        if (option.has("mute")) {
            opt_mute = option.getBoolean("mute");
        }

        Runnable delNap = () -> {
            try {
                if (alarmSchedule.removeSchedule(id)) {
                    refreshAlarmList();
                } else {
                    Toast.makeText(getContext(), getString(R.string.fragment_main_alarm__msg_failed_to_del_alarm), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        };

        LinearLayout parent = new LinearLayout(getContext());
        parent.setBackgroundDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.bg_set_alarm_card));
        parent.setClickable(true);
        parent.setGravity(Gravity.CENTER_VERTICAL);
        parent.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        parent.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext(), R.style.SomnificusAlertDialogTheme)
                    .setCancelable(false)
                    .setTitle(R.string.fragment_main_alarm__dialog_del_alarm_title)
                    .setMessage(R.string.fragment_main_alarm__dialog_del_alarm_msg)
                    .setNegativeButton(R.string.common__text_no, null)
                    .setPositiveButton(R.string.common__text_yes, (dialog, which) -> delNap.run())
                    .show();
        });
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)lp;
        mlp.setMargins(mlp.leftMargin, Unit.Pixel.dp2px(requireContext(), 8), mlp.rightMargin, Unit.Pixel.dp2px(requireContext(), 8));
        parent.setLayoutParams(mlp);

        TextView text_time = new TextView(requireContext());
        text_time.setText(String.format("%02d:%02d", time.getInt(0), time.getInt(1)));
        text_time.setTextSize(28);
        text_time.setTextColor(requireContext().getColor(R.color.white));

        TextView text_date = new TextView(requireContext());
        text_date.setText(String.format("%s - %s", getString(R.string.fragment_main_alarm__text_today), getString(R.string.fragment_main_alarm__fab_dtext_nap)));
        text_date.setTextColor(requireContext().getColor(R.color.white));

        LinearLayout child_mainpanel = new LinearLayout(requireContext());
        child_mainpanel.setOrientation(LinearLayout.HORIZONTAL);
        child_mainpanel.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );

        LinearLayout child_schedule = new LinearLayout(requireContext());
        child_schedule.setOrientation(LinearLayout.VERTICAL);
        child_schedule.addView(text_time);
        child_schedule.addView(text_date);

        LinearLayout rightView = new LinearLayout(getContext());
        rightView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        rightView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (opt_mute) {
            ImageView icon_mute = new ImageView(getContext());
            icon_mute.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_volume_off));
            icon_mute.setImageTintList(ColorStateList.valueOf(iconTintColor));
            icon_mute.setLayoutParams(new LinearLayout.LayoutParams(
                    Unit.Pixel.dp2px(requireContext(), 24),
                    Unit.Pixel.dp2px(requireContext(), 24)
            ));
            icon_mute.setPadding(
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2),
                    Unit.Pixel.dp2px(requireContext(), 2)
            );
            rightView.addView(icon_mute);
        }

        ImageView deleteBtn = new ImageView(getContext());
        deleteBtn.setBackgroundResource(rippleEffect.resourceId);
        deleteBtn.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_delete));
        deleteBtn.setImageTintList(ColorStateList.valueOf(iconTintColor));
        deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(
                Unit.Pixel.dp2px(requireContext(), 48),
                Unit.Pixel.dp2px(requireContext(), 48)
        ));
        deleteBtn.setPadding(
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8),
                Unit.Pixel.dp2px(requireContext(), 8)
        );
        rightView.addView(deleteBtn);

        child_mainpanel.addView(child_schedule);
        child_mainpanel.addView(rightView);
        parent.addView(child_mainpanel);

        return parent;
    }

    private void refreshAlarmList() {
        refreshAlarmList(false);
    }

    private void refreshAlarmList(boolean checked_license) {
        LinearLayout scheduleView = ROOT.findViewById(R.id.AlarmFragment__Schedule_View);
        scheduleView.removeAllViews();

        try {
            if (getString(R.string.build_type).equals("DEBUG")) {
                ROOT.findViewById(R.id.AlarmFragment__DebugMsg).setVisibility(View.VISIBLE);
            }

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
                if (object.getInt("type") == AlarmSchedule.TYPE__NAP) {
                    LinearLayout view = createNapView(object);
                    scheduleView.addView(view);
                }
            }
            int num_of_alarm = 0;
            for (int k = 0; k < alarmList.size(); k++) {
                JSONObject object = alarmList.get(k);
                if (object.getInt("type") == AlarmSchedule.TYPE__ALARM) {
                    num_of_alarm++;
                    LinearLayout view = createAlarmView(object);
                    scheduleView.addView(view);
                    if (checked_license && !IS_PREMIUM && num_of_alarm % 10 == 1) {
                        AdRequest adRequest = new AdRequest.Builder().build();
                        AdView adView = new AdView(requireContext());
                        adView.setAdSize(AdSize.BANNER);
                        adView.setAdUnitId(getString(R.string.ads_id_ll_banner));
                        adView.loadAd(adRequest);
                        scheduleView.addView(adView);
                    }
                }
            }
            alarmSchedule.sync();
        } catch (JSONException | IOException e) {
            Toast.makeText(requireContext(), R.string.fragment_main_alarm__toast_failed_get_alarm, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException | IllegalStateException e) {
            Log.w("AlarmFragment", e.toString());
        }
    }

    private void initUI() {
        IS_FAB_CLICKED = false;

        FloatingActionButton fab = ROOT.findViewById(R.id.AlarmFragment__FloatingActionButton);
        FloatingActionButton subfab_alarm = ROOT.findViewById(R.id.AlarmFragment__SubFAB_Alarm);
        FloatingActionButton subfab_nap = ROOT.findViewById(R.id.AlarmFragment__SubFAB_Nap);
        LinearLayout l_subfab_alarm = ROOT.findViewById(R.id.AlarmFragment__L_SubFAB_Alarm);
        LinearLayout l_subfab_nap = ROOT.findViewById(R.id.AlarmFragment__L_SubFAB_Nap);

        fab.animate().rotation(0).setDuration(0);
        l_subfab_alarm.animate().alpha(0).setDuration(0);
        l_subfab_nap.animate().alpha(0).setDuration(0);

        fab.setOnClickListener(v -> {
            IS_FAB_CLICKED = !IS_FAB_CLICKED;
            if (IS_FAB_CLICKED) {
                fab.animate().rotationBy(0).rotation(45).setDuration(250);
                l_subfab_alarm.animate().alphaBy(0).alpha(1).setDuration(250).withStartAction(() -> l_subfab_alarm.setVisibility(View.VISIBLE));
                l_subfab_nap.animate().alphaBy(0).alpha(1).setDuration(250).withStartAction(() -> l_subfab_nap.setVisibility(View.VISIBLE));
            } else {
                fab.animate().rotationBy(45).rotation(0).setDuration(250);
                l_subfab_alarm.animate().alphaBy(1).alpha(0).setDuration(250).withEndAction(() -> l_subfab_alarm.setVisibility(View.INVISIBLE));
                l_subfab_nap.animate().alphaBy(1).alpha(0).setDuration(250).withEndAction(() -> l_subfab_nap.setVisibility(View.INVISIBLE));
            }
        });
        subfab_alarm.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetAlarmActivity.class);
            intent.putExtra("id", -1);
            startActivity(intent);
        });
        subfab_nap.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SetNapActivity.class);
            intent.putExtra("id", -1);
            startActivity(intent);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ROOT = inflater.inflate(R.layout.fragment_main_alarm, container, false);

        billingManager = new BillingManager(getActivity());
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            billingManager.queryPurchaseAsync(
                    BillingClient.ProductType.SUBS,
                    BillingManager.ProductId.SUBS_PREMIUM,
                    (billingResult, purchased) -> IS_PREMIUM = purchased
            );
        }).start();

        initUI();
        new Handler().postDelayed(() -> refreshAlarmList(true), 1000);

        return ROOT;
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
        refreshAlarmList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
        // TODO: Use the ViewModel
    }

}