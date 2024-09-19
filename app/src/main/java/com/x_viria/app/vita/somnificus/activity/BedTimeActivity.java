package com.x_viria.app.vita.somnificus.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.ui.overlay.PopupNumPicker;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPDefault;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

public class BedTimeActivity extends AppCompatActivity {

    private static final int FLAG_SUN = 1;
    private static final int FLAG_MON = 1 << 1;
    private static final int FLAG_TUE = 1 << 2;
    private static final int FLAG_WED = 1 << 3;
    private static final int FLAG_THU = 1 << 4;
    private static final int FLAG_FRI = 1 << 5;
    private static final int FLAG_SAT = 1 << 6;

    private CheckBox ChkBox_Sun;
    private CheckBox ChkBox_Mon;
    private CheckBox ChkBox_Tue;
    private CheckBox ChkBox_Wed;
    private CheckBox ChkBox_Thu;
    private CheckBox ChkBox_Fri;
    private CheckBox ChkBox_Sat;

    private void revealView(View view) {
        view.setVisibility(View.VISIBLE);

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.requestLayout();

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.start();
    }

    private void hideView(final View view) {
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), 0);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void refresh() {
        SPStorage sps = new SPStorage(this);

        ((TextView) findViewById(R.id.BedTimeActivity__Remind_M)).setText(
                String.format(
                        getString(R.string.activity_bed_time__text_remind_format),
                        sps.getLong(Config.KEY__SETTINGS_BED_TIME_REMIND, SPDefault.SETTINGS_BED_TIME_REMIND) / 60 / 1000
                )
        );

        int flag_dow = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
        if ((flag_dow & FLAG_SUN) == FLAG_SUN) {
            ChkBox_Sun.setChecked(true);
        } if ((flag_dow & FLAG_MON) == FLAG_MON) {
            ChkBox_Mon.setChecked(true);
        } if ((flag_dow & FLAG_TUE) == FLAG_TUE) {
            ChkBox_Tue.setChecked(true);
        } if ((flag_dow & FLAG_WED) == FLAG_WED) {
            ChkBox_Wed.setChecked(true);
        } if ((flag_dow & FLAG_THU) == FLAG_THU) {
            ChkBox_Thu.setChecked(true);
        } if ((flag_dow & FLAG_FRI) == FLAG_FRI) {
            ChkBox_Fri.setChecked(true);
        } if ((flag_dow & FLAG_SAT) == FLAG_SAT) {
            ChkBox_Sat.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_time);

        ChkBox_Sun = findViewById(R.id.BedTimeActivity__Time_Sun_CheckBox);
        ChkBox_Mon = findViewById(R.id.BedTimeActivity__Time_Mon_CheckBox);
        ChkBox_Tue = findViewById(R.id.BedTimeActivity__Time_Tue_CheckBox);
        ChkBox_Wed = findViewById(R.id.BedTimeActivity__Time_Wed_CheckBox);
        ChkBox_Thu = findViewById(R.id.BedTimeActivity__Time_Thu_CheckBox);
        ChkBox_Fri = findViewById(R.id.BedTimeActivity__Time_Fri_CheckBox);
        ChkBox_Sat = findViewById(R.id.BedTimeActivity__Time_Sat_CheckBox);

        SPStorage sps = new SPStorage(this);
        boolean enable_remind = sps.getBool(Config.KEY__SETTINGS_ENABLE_BED_TIME_REMIND, SPDefault.SETTINGS_ENABLE_BED_TIME_REMIND);

        findViewById(R.id.BedTimeActivity__Btn_Back).setOnClickListener(v -> finish());

        SwitchMaterial Switch_Enable = findViewById(R.id.BedTimeActivity__Enable_Switch);
        findViewById(R.id.BedTimeActivity__Enable_LL).setOnClickListener(v -> Switch_Enable.setChecked(!Switch_Enable.isChecked()));

        Switch_Enable.setChecked(enable_remind);

        LinearLayout LL_Remind = findViewById(R.id.BedTimeActivity__Remind);
        LL_Remind.setVisibility(enable_remind ? View.VISIBLE : View.GONE);
        LL_Remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupNumPicker popupNumPicker = new PopupNumPicker(BedTimeActivity.this);
                popupNumPicker.setMaximum(60);
                popupNumPicker.setMinimum(15);
                popupNumPicker.setPositiveButton(new PopupNumPicker.OnClickListener() {
                    @Override
                    public void onClick(int n) {
                        super.onClick(n);
                        sps.setLong(Config.KEY__SETTINGS_BED_TIME_REMIND, (long) n * 60 * 1000);
                        refresh();
                    }
                });
                popupNumPicker.show(v);
            }
        });

        Switch_Enable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sps.setBool(Config.KEY__SETTINGS_ENABLE_BED_TIME_REMIND, isChecked);
            if (!isChecked) {
                hideView(LL_Remind);
            } else {
                revealView(LL_Remind);
            }
        });

        ChkBox_Sun.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_SUN;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Mon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_MON;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Tue.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_TUE;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Wed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_WED;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Thu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_THU;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Fri.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_FRI;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });
        ChkBox_Sat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int flag = sps.getInt(Config.KEY__SETTINGS_BED_TIME_DOW, SPDefault.SETTINGS_BED_TIME_DOW);
            int mask = FLAG_SAT;
            if (isChecked) {
                flag |= mask;
            } else {
                flag &= ~mask;
            }
            sps.setInt(Config.KEY__SETTINGS_BED_TIME_DOW, flag);
        });

        refresh();
    }

}