package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;

import com.x_viria.app.vita.somnificus.R;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class AlarmSchedule {

    private final Context CONTEXT;
    private JSONObject OBJECT;

    public static final int TYPE__ALARM = 0x01;
    public static final int TYPE__NAP = 0x02;

    public AlarmSchedule(Context context) throws JSONException, IOException {
        this.CONTEXT = context;

        String data = new SPStorage(context).getString(Config.KEY__ALARM_SCHEDULE, "");

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

    private JSONObject makeObject(AlarmInfo info, int schedule_id, int type) throws JSONException {
        JSONObject json = new JSONObject();
        JSONArray time = new JSONArray();
        time.put(info.getAlarmTime().getH());
        time.put(info.getAlarmTime().getM());
        time.put(info.getAlarmTime().getS());
        json.put("type", type);
        json.put("time", time);
        json.put("schedule_id", schedule_id);
        json.put("data", new JSONObject());
        return json;
    }

    private JSONObject makeAlarmObject(AlarmInfo info, int schedule_id) throws JSONException {
        JSONObject object = makeObject(info, schedule_id, TYPE__ALARM);
        JSONObject json = new JSONObject();
        json.put("label", info.getLabel());
        json.put("week", info.getWeek());
        json.put("enable", info.getEnable());
        JSONObject option = new JSONObject();
        option.put("gra_increase_vol", info.getOption(AlarmInfo.OPT__GRA_INCREASE_VOL));
        json.put("option", option);
        object.put("data", json);
        return object;
    }

    private JSONObject makeNapObject(AlarmInfo info, int schedule_id) throws JSONException {
        JSONObject object = makeObject(info, schedule_id, TYPE__NAP);
        JSONObject json = new JSONObject();
        JSONObject option = new JSONObject();
        option.put("gra_increase_vol", info.getOption(AlarmInfo.OPT__GRA_INCREASE_VOL));
        json.put("option", option);
        object.put("data", json);
        return object;
    }

    private void save() throws JSONException {
        String json = OBJECT.toString();
        new SPStorage(CONTEXT).setString(Config.KEY__ALARM_SCHEDULE, json);
        sync();
    }

    public boolean equals(AlarmInfo info1, AlarmInfo info2) {
        try {
            String j1 = makeAlarmObject(info1, 0).toString();
            String j2 = makeAlarmObject(info2, 0).toString();
            return j1.equals(j2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public AlarmInfo getNextAlarm() throws JSONException {
        JSONArray list = getSchedule();
        long m_ms = Long.MAX_VALUE;
        AlarmInfo info = null;
        for (int i = 0; i < list.length(); i++) {
            JSONObject obj = list.getJSONObject(i);
            JSONArray time = obj.getJSONArray("time");
            AlarmInfo aInfo = null;
            if (obj.getInt("type") == TYPE__ALARM) {
                JSONObject objdata = obj.getJSONObject("data");
                aInfo = new AlarmInfo(
                        new AlarmTime(time.getInt(0), time.getInt(1), time.getInt(2)),
                        objdata.getInt("week"),
                        objdata.getBoolean("enable")
                );
            } else if (obj.getInt("type") == TYPE__NAP) {
                aInfo = new AlarmInfo(
                        new AlarmTime(time.getInt(0), time.getInt(1), time.getInt(2)),
                        AlarmInfo.WEEK__ALL,
                        true
                );
            }
            long ms = aInfo.getNextTime();
            if (ms < m_ms) {
                m_ms = ms;
                info = aInfo;
            }
        }
        return info;
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
        String data = new SPStorage(CONTEXT).getString(Config.KEY__ALARM_SCHEDULE, "");
        Log.d("AlarmSchedule", data);
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
        setEnable(id, false);
        boolean tf = false;
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            if (object.getInt("type") == TYPE__ALARM) {
                list.remove(i);
                save();
                tf = true;
            } else if (object.getInt("type") == TYPE__NAP) {
                AlarmManager alarmManager = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(CONTEXT.getApplicationContext(), AlarmBroadcastReceiver.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", id);
                intent.putExtra("title", "Test Alarm");
                PendingIntent pending = PendingIntent.getBroadcast(CONTEXT.getApplicationContext(), id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                pending.cancel();
                alarmManager.cancel(pending);
                list.remove(i);
                save();
                tf = true;
            }
        }
        OBJECT.put("schedule", list);
        return tf;
    }

    public boolean setEnable(int id, boolean enable) throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("type") != TYPE__ALARM) continue;
            if (object.getInt("schedule_id") != id) continue;
            JSONObject objdata = object.getJSONObject("data");
            objdata.put("enable", enable);
            object.put("data", objdata);
            OBJECT.getJSONArray("schedule").put(i, object);
            save();
            return true;
        }
        return false;
    }

    public void setAlarmSchedule(AlarmInfo info) throws JSONException {
        Random rand = new Random();
        int schedule_id = rand.nextInt(Integer.MAX_VALUE);
        int cnt = 0;
        while (!canRegisterId(schedule_id)) {
            if (255 < cnt) return;
            schedule_id = rand.nextInt(Integer.MAX_VALUE);
            cnt++;
        }
        JSONObject object = makeAlarmObject(info, schedule_id);
        OBJECT.getJSONArray("schedule").put(object);
        save();
    }

    public void setAlarmSchedule(AlarmInfo info, int id) throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            object = makeAlarmObject(info, id);
            OBJECT.getJSONArray("schedule").put(i, object);
            save();
        }
    }

    public void setNapSchedule(AlarmInfo info) throws JSONException {
        Random rand = new Random();
        int schedule_id = rand.nextInt(Integer.MAX_VALUE);
        int cnt = 0;
        while (!canRegisterId(schedule_id)) {
            if (255 < cnt) return;
            schedule_id = rand.nextInt(Integer.MAX_VALUE);
            cnt++;
        }
        JSONObject object = makeNapObject(info, schedule_id);
        OBJECT.getJSONArray("schedule").put(object);
        save();
    }

    public void sync() throws JSONException {
        JSONArray list = getSchedule();
        Log.d("AlarmSchedule", list.toString(4));
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            int schedule_id = object.getInt("schedule_id");
            AlarmManager alarmManager = (AlarmManager) CONTEXT.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(CONTEXT.getApplicationContext(), AlarmBroadcastReceiver.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", schedule_id);
            intent.putExtra("title", "Test Alarm");
            PendingIntent pending = PendingIntent.getBroadcast(CONTEXT.getApplicationContext(), schedule_id, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            int schedule_type = object.getInt("type");
            AlarmTime alarmTime = new AlarmTime(
                    object.getJSONArray("time").getInt(0),
                    object.getJSONArray("time").getInt(1),
                    object.getJSONArray("time").getInt(2)
            );
            JSONObject objdata = object.getJSONObject("data");

            if (schedule_type == TYPE__ALARM) {
                AlarmInfo alarmInfo = new AlarmInfo(
                        alarmTime,
                        objdata.getInt("week"),
                        objdata.getBoolean("enable")
                );
                AlarmManager.AlarmClockInfo acInfo = new AlarmManager.AlarmClockInfo(alarmInfo.getCalendar().getTimeInMillis(), null);

                if (objdata.getBoolean("enable")) {
                    alarmManager.setAlarmClock(acInfo, pending);
                } else {
                    pending.cancel();
                    alarmManager.cancel(pending);
                }
            } else if (schedule_type == TYPE__NAP) {
                AlarmInfo alarmInfo = new AlarmInfo(
                        alarmTime,
                        AlarmInfo.WEEK__ALL,
                        true
                );
                AlarmManager.AlarmClockInfo acInfo = new AlarmManager.AlarmClockInfo(alarmInfo.getCalendar().getTimeInMillis(), null);
                alarmManager.setAlarmClock(acInfo, pending);
            } else {
            }
        }
    }

}
