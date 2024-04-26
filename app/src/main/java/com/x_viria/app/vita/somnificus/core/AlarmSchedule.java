package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.SPKey;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class AlarmSchedule {

    private final Context CONTEXT;
    private JSONObject OBJECT;

    public AlarmSchedule(Context context) throws JSONException, IOException {
        this.CONTEXT = context;

        String data = new SPStorage(context).getString(SPKey.KEY__ALARM_SCHEDULE, "");

        if (data.equals("")) {
            data = "{'format_version': 0, 'schedule': []}";
            OBJECT = new JSONObject(data);
            save();
        } else {
            OBJECT = new JSONObject(data);
        }
    }

    private boolean canRegisterId(int id) {
        try {
            JSONArray list = getSchedule();
            for (int i = 0; i < list.length(); i++) {
                JSONObject object = list.getJSONObject(i);
                int schedule_id = object.getInt("schedule_id");
                if (schedule_id == id) return false;
            }
            return true;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject makeObject(AlarmInfo info, int schedule_id) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray time = new JSONArray();
        time.put(info.getAlarmTime().getH());
        time.put(info.getAlarmTime().getM());
        time.put(info.getAlarmTime().getS());
        json.put("label", info.getLabel());
        json.put("time", time);
        json.put("week", info.getWeek());
        json.put("enable", info.getEnable());
        json.put("schedule_id", schedule_id);
        JSONObject option = new JSONObject();
        option.put("gra_increase_vol", info.getOption(AlarmInfo.OPT__GRA_INCREASE_VOL));
        json.put("option", option);
        return json;
    }

    private void save() {
        String json = OBJECT.toString();
        new SPStorage(CONTEXT).setString(SPKey.KEY__ALARM_SCHEDULE, json);
    }

    public boolean equals(AlarmInfo info1, AlarmInfo info2) {
        try {
            String j1 = makeObject(info1, 0).toString();
            String j2 = makeObject(info2, 0).toString();
            return j1.equals(j2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNextDate(AlarmInfo alarmInfo) {
        Calendar calendarToday = Calendar.getInstance();
        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DATE, 1);
        String[] label_DofW = {
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_sun),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_mon),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_tue),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_wed),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_thu),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_fri),
                CONTEXT.getString(R.string.fragment_main_alarm__text_dofw_sat)
        };
        String targetDay = null;
        if (!alarmInfo.getEnable()) {
            targetDay = CONTEXT.getString(R.string.fragment_main_alarm__text_not_scheduled);
        } else if (
                alarmInfo.getCalendar().get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR)
                        && alarmInfo.getCalendar().get(Calendar.MONTH) == calendarToday.get(Calendar.MONTH)
                        && alarmInfo.getCalendar().get(Calendar.DATE) == calendarToday.get(Calendar.DATE)
        ) {
            targetDay = CONTEXT.getString(R.string.fragment_main_alarm__text_today);
        } else if (
                alarmInfo.getCalendar().get(Calendar.YEAR) == calendarTomorrow.get(Calendar.YEAR)
                        && alarmInfo.getCalendar().get(Calendar.MONTH) == calendarTomorrow.get(Calendar.MONTH)
                        && alarmInfo.getCalendar().get(Calendar.DATE) == calendarTomorrow.get(Calendar.DATE)
        ) {
            targetDay = CONTEXT.getString(R.string.fragment_main_alarm__text_tomorrow);
        } else if (alarmInfo.getCalendar().after(calendarTomorrow)) {
            targetDay = label_DofW[alarmInfo.getCalendar().get(Calendar.DAY_OF_WEEK) - 1];
        }
        return targetDay;
    }

    public JSONArray getSchedule() throws JSONException {
        String data = new SPStorage(CONTEXT).getString(SPKey.KEY__ALARM_SCHEDULE, "");
        OBJECT = new JSONObject(data);
        return OBJECT.getJSONArray("schedule");
    }

    public JSONObject getSchedule(int id) throws JSONException {
        JSONArray schedule = getSchedule();
        for (int i = 0; i < schedule.length(); i++) {
            JSONObject object = schedule.getJSONObject(i);
            if (object.getInt("schedule_id") == id) return object;
        }
        return null;
    }

    public int getScheduledNum() throws JSONException {
        return getSchedule().length();
    }

    public boolean removeSchedule(int id) throws JSONException {
        sync();
        boolean tf = false;
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            list.remove(i);
            tf = true;
        }
        save();
        return tf;
    }

    public void setEnable(int id, boolean enable) throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            object.put("enable", enable);
            OBJECT.getJSONArray("schedule").put(i, object);
            save();
        }
    }

    public boolean setSchedule(AlarmInfo info) throws JSONException, IOException {
        Random rand = new Random();
        int schedule_id = rand.nextInt(Integer.MAX_VALUE);
        int cnt = 0;
        while (!canRegisterId(schedule_id)) {
            if (255 < cnt) return false;
            schedule_id = rand.nextInt(Integer.MAX_VALUE);
            cnt++;
        }
        JSONObject object = makeObject(info, schedule_id);
        OBJECT.getJSONArray("schedule").put(object);
        save();
        return true;
    }

    public void setSchedule(AlarmInfo info, int id) throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            object = makeObject(info, id);
            OBJECT.getJSONArray("schedule").put(i, object);
            save();
        }
    }

    public void sync() throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            int schedule_id = object.getInt("schedule_id");
            AlarmManager alarmManager = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(CONTEXT.getApplicationContext(), AlarmBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", schedule_id);
            intent.putExtra("title", "Test Alarm");
            PendingIntent pending = PendingIntent.getBroadcast(CONTEXT.getApplicationContext(), schedule_id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmTime alarmTime = new AlarmTime(
                    object.getJSONArray("time").getInt(0),
                    object.getJSONArray("time").getInt(1),
                    object.getJSONArray("time").getInt(2)
            );
            AlarmInfo alarmInfo = new AlarmInfo(
                    alarmTime,
                    object.getInt("week"),
                    object.getBoolean("enable")
            );
            AlarmManager.AlarmClockInfo acInfo = new AlarmManager.AlarmClockInfo(alarmInfo.getCalendar().getTimeInMillis(), null);

            if (object.getBoolean("enable")) {
                alarmManager.setAlarmClock(acInfo, pending);
            } else {
                pending.cancel();
                alarmManager.cancel(pending);
            }
        }
    }

}
