package com.x_viria.app.vita.somnificus.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.backup.BackupManager;
import android.content.res.ColorStateList;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationInfo;
import com.x_viria.app.vita.somnificus.core.sda.SleepDurationManager;

import org.json.JSONException;

import java.util.Calendar;

public class SetSleepDurationActivity extends AppCompatActivity {

    private int EVAL_GOOD_OR_BAD = SleepDurationInfo.Eval.NONE;

    private int[] SD_BT_DATE = new int[] {0, 0, 0};
    private int[] SD_BT_TIME = new int[] {23, 0};
    private int[] SD_WT_DATE = new int[] {0, 0, 0};
    private int[] SD_WT_TIME = new int[] {7, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sleep_duration);

        Calendar c = Calendar.getInstance();
        Calendar cy = Calendar.getInstance();
        cy.add(Calendar.DAY_OF_MONTH, -1);
        setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), R.id.SetSDActivity__SD_WT_Date);
        setDate(cy.get(Calendar.YEAR), cy.get(Calendar.MONTH), cy.get(Calendar.DAY_OF_MONTH), R.id.SetSDActivity__SD_BT_Date);

        ImageView IVBtn_Back_Btn = findViewById(R.id.SetSDActivity__Back_Btn);
        IVBtn_Back_Btn.setOnClickListener(v -> finish());

        TextView TVBtn_Save_Btn = findViewById(R.id.SetSDActivity__Save_Btn);
        TVBtn_Save_Btn.setOnClickListener(v -> {
            ProgressDialog dialog = new ProgressDialog(SetSleepDurationActivity.this);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.common__text_saving));
            dialog.setIndeterminate(true);
            dialog.show();

            Handler handler = new Handler();
            handler.post(() -> {
                Calendar C_BedTime = Calendar.getInstance();
                C_BedTime.set(SD_BT_DATE[0], SD_BT_DATE[1], SD_BT_DATE[2], SD_BT_TIME[0], SD_BT_TIME[1], 0);
                Calendar C_WakeupTime = Calendar.getInstance();
                C_WakeupTime.set(SD_WT_DATE[0], SD_WT_DATE[1], SD_WT_DATE[2], SD_WT_TIME[0], SD_WT_TIME[1], 0);

                if (
                        24 * 60 * 60 * 1000 < C_WakeupTime.getTimeInMillis() - C_BedTime.getTimeInMillis()
                        || C_WakeupTime.getTimeInMillis() <= C_BedTime.getTimeInMillis()
                ) {
                    dialog.dismiss();
                    new AlertDialog.Builder(SetSleepDurationActivity.this)
                            .setCancelable(false)
                            .setTitle(R.string.activity_set_sleep_duration__dialog_invalid_time_title)
                            .setMessage(R.string.activity_set_sleep_duration__dialog_invalid_time_msg)
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                try {
                    SleepDurationInfo sdi = new SleepDurationInfo(C_BedTime.getTimeInMillis(), C_WakeupTime.getTimeInMillis());
                    sdi.EVAL__GOOD_OR_BAD = EVAL_GOOD_OR_BAD;
                    SleepDurationManager sdm = new SleepDurationManager(SetSleepDurationActivity.this);
                    sdm.add(sdi);
                    dialog.dismiss();
                    BackupManager backupManager = new BackupManager(this);
                    backupManager.dataChanged();
                    finish();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        TextView TV_SDBT_Date = findViewById(R.id.SetSDActivity__SD_BT_Date);
        TV_SDBT_Date.setOnClickListener(v -> {
            DatePickerDialogFragment dpdf = new DatePickerDialogFragment(R.id.SetSDActivity__SD_BT_Date);
            dpdf.show(getSupportFragmentManager(), "datePicker");
        });

        TextView TV_SDBT_Time = findViewById(R.id.SetSDActivity__SD_BT_Time);
        TV_SDBT_Time.setOnClickListener(v -> {
            TimePickerDialogFragment dpdf = new TimePickerDialogFragment(R.id.SetSDActivity__SD_BT_Time);
            dpdf.show(getSupportFragmentManager(), "datePicker");
        });

        TextView TV_SDWT_Date = findViewById(R.id.SetSDActivity__SD_WT_Date);
        TV_SDWT_Date.setOnClickListener(v -> {
            DatePickerDialogFragment dpdf = new DatePickerDialogFragment(R.id.SetSDActivity__SD_WT_Date);
            dpdf.show(getSupportFragmentManager(), "datePicker");
        });

        TextView TV_SDWT_Time = findViewById(R.id.SetSDActivity__SD_WT_Time);
        TV_SDWT_Time.setOnClickListener(v -> {
            TimePickerDialogFragment dpdf = new TimePickerDialogFragment(R.id.SetSDActivity__SD_WT_Time);
            dpdf.show(getSupportFragmentManager(), "datePicker");
        });

        LinearLayout LLBtn_Good = findViewById(R.id.SetSDActivity__Good_Btn);
        ImageView IV_Good_Icon = findViewById(R.id.SetSDActivity__Good_Icon);
        TextView TV_Good_Text = findViewById(R.id.SetSDActivity__Good_Text);
        LinearLayout LLBtn_Bad = findViewById(R.id.SetSDActivity__Bad_Btn);
        ImageView IV_Bad_Icon = findViewById(R.id.SetSDActivity__Bad_Icon);
        TextView TV_Bad_Text = findViewById(R.id.SetSDActivity__Bad_Text);
        LLBtn_Good.setOnClickListener(v -> {
            IV_Good_Icon.setImageTintList(ColorStateList.valueOf(getColor(R.color.selectedColorOrange)));
            TV_Good_Text.setTextColor(getColor(R.color.selectedColorOrange));
            IV_Bad_Icon.setImageTintList(ColorStateList.valueOf(getColor(R.color.primaryInactiveColor)));
            TV_Bad_Text.setTextColor(getColor(R.color.primaryInactiveColor));
            EVAL_GOOD_OR_BAD = SleepDurationInfo.Eval.GOOD;
        });
        LLBtn_Bad.setOnClickListener(v -> {
            IV_Good_Icon.setImageTintList(ColorStateList.valueOf(getColor(R.color.primaryInactiveColor)));
            TV_Good_Text.setTextColor(getColor(R.color.primaryInactiveColor));
            IV_Bad_Icon.setImageTintList(ColorStateList.valueOf(getColor(R.color.selectedColorAqua)));
            TV_Bad_Text.setTextColor(getColor(R.color.selectedColorAqua));
            EVAL_GOOD_OR_BAD = SleepDurationInfo.Eval.BAD;
        });
    }

    private void setDate(int year, int month, int date, int id) {
        if (id == R.id.SetSDActivity__SD_BT_Date) {
            SD_BT_DATE[0] = year;
            SD_BT_DATE[1] = month;
            SD_BT_DATE[2] = date;
        } else if (id == R.id.SetSDActivity__SD_WT_Date) {
            SD_WT_DATE[0] = year;
            SD_WT_DATE[1] = month;
            SD_WT_DATE[2] = date;
        }
    }

    private void setTime(int hour, int min, int id) {
        if (id == R.id.SetSDActivity__SD_BT_Time) {
            SD_BT_TIME[0] = hour;
            SD_BT_TIME[1] = min;
        } else if (id == R.id.SetSDActivity__SD_WT_Time) {
            SD_WT_TIME[0] = hour;
            SD_WT_TIME[1] = min;
        }
    }

    private void setText(String text, int id) {
        TextView TV = findViewById(id);
        TV.setText(text);
    }

    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        private final int ID;

        public DatePickerDialogFragment(int id) {
            this.ID = id;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(requireActivity(), R.style.SomnificusDatePickerDialogTheme, this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            final Calendar c = Calendar.getInstance();
            final Calendar cy = Calendar.getInstance();
            final Calendar cn = Calendar.getInstance();
            cn.set(Calendar.YEAR, year);
            cn.set(Calendar.MONTH, month);
            cn.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cy.add(Calendar.DAY_OF_MONTH, -1);
            if (c.equals(cn)) {
                ((SetSleepDurationActivity) getActivity()).setText(getString(R.string.fragment_main_alarm__text_today), ID);
            } else if (cy.equals(cn)) {
                ((SetSleepDurationActivity) getActivity()).setText(getString(R.string.common__text_yesterday), ID);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.common__text_date_format));
                ((SetSleepDurationActivity) getActivity()).setText(String.format(sdf.format(cn.getTime())), ID);
            }
            ((SetSleepDurationActivity) getActivity()).setDate(year, month, dayOfMonth, ID);
        }

    }

    public static class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        private final int ID;

        public TimePickerDialogFragment(int id) {
            this.ID = id;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            return new TimePickerDialog(requireActivity(), R.style.SomnificusTimePickerDialogTheme, this, hour, min, true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            final Calendar cn = Calendar.getInstance();
            cn.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cn.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            ((SetSleepDurationActivity) getActivity()).setText(String.format(sdf.format(cn.getTime())), ID);
            ((SetSleepDurationActivity) getActivity()).setTime(hourOfDay, minute, ID);
        }

    }

}