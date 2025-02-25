package com.x_viria.app.vita.somnificus.util.storage;

import com.x_viria.app.vita.somnificus.R;

public class Config {

    public static final String KEY__ALARM_SCHEDULE = "SOMNIFICUS_SHAREDP_KEY__ALARM_SCHEDULE";
    public static final String KEY__SLEEP_DURATION = "SOMNIFICUS_SHAREDP_KEY__SLEEP_DURATION";

    public static final String KEY__SETTINGS_ALARM_VIBRATE = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_ALARM_VIBRATE";
    public static final String KEY__SETTINGS_TIMER_SOUND = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_TIMER_SOUND";
    public static final String KEY__SETTINGS_TIMER_VIBRATE = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_TIMER_VIBRATE";
    public static final String KEY__SETTINGS_REMIND_SAVESD = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_REMIND_SAVESD";
    public static final String KEY__SETTINGS_REMIND_SET_ALARM = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_REMIND_SET_ALARM";

    public static final String KEY__SETTINGS_ENABLE_BED_TIME_REMIND = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_ENABLE_BED_TIME_REMIND";
    public static final String KEY__SETTINGS_BED_TIME_REMIND = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_REMIND";
    public static final String KEY__SETTINGS_BED_TIME_DOW = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_DOW";
    public static final String KEY__SETTINGS_BED_TIME_D_SUN = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_SUN";
    public static final String KEY__SETTINGS_BED_TIME_D_MON = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_MON";
    public static final String KEY__SETTINGS_BED_TIME_D_TUE = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_TUE";
    public static final String KEY__SETTINGS_BED_TIME_D_WED = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_WED";
    public static final String KEY__SETTINGS_BED_TIME_D_THU = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_THU";
    public static final String KEY__SETTINGS_BED_TIME_D_FRI = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_FRI";
    public static final String KEY__SETTINGS_BED_TIME_D_SAT = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_BED_TIME_D_SAT";
    public static final String KEY__SETTINGS_APP_THEME = "SOMNIFICUS_SHAREDP_KEY__SETTINGS_APP_THEME";

    public static final String KEY__LAST_BACKUP_DATE = "SOMNIFICUS_SHAREDP_KEY__LAST_BACKUP_DATE";
    public static final String KEY__LAST_BACKUP_SIZE = "SOMNIFICUS_SHAREDP_KEY__LAST_BACKUP_SIZE";

    public static final String KEY__UD_BED_TIME = "SOMNIFICUS_SHAREDP_KEY__UD_BED_TIME";

    public static final String TMP__TIMER_VAL = "SOMNIFICUS_SHAREDP_TMP__TIMER_VAL";

    public static class Theme {
        public static final String[] LABELS = new String[] {"Default", "Vivid"};
        public static final int[] VALUES = new int[] {R.style.Theme_Somnificus, R.style.SomnificusVivid};
    }

}
