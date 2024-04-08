package com.x_viria.app.vita.somnificus.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class AlarmSchedule {

    private final Context CONTEXT;
    private JSONObject OBJECT;

    private final String SP_KEY__ALARM = "Somnificus_SharedP_Key__Alarm_Schedule";

    public AlarmSchedule(Context context) throws JSONException, IOException {
        this.CONTEXT = context;

        String data = new SPStorage(context).getString(SP_KEY__ALARM, "");

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

    private void save() {
        String json = OBJECT.toString();
        Log.d("AlarmSchedule", "JSON -> " + json);
        new SPStorage(CONTEXT).setString(SP_KEY__ALARM, json);
    }

    public JSONArray getSchedule() throws JSONException {
        String data = new SPStorage(CONTEXT).getString(SP_KEY__ALARM, "");
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

    public boolean setSchedule(AlarmInfo info) throws JSONException, IOException {
        Random rand = new Random();
        int schedule_id = rand.nextInt(Integer.MAX_VALUE);
        int cnt = 0;
        while (!canRegisterId(schedule_id)) {
            if (255 < cnt) return false;
            schedule_id = rand.nextInt(Integer.MAX_VALUE);
            cnt++;
        }
        JSONObject json = new JSONObject();
        JSONArray time = new JSONArray();
        time.put(info.getAlarmTime().getH());
        time.put(info.getAlarmTime().getM());
        time.put(info.getAlarmTime().getS());
        json.put("time", time);
        json.put("week", info.getWeek());
        json.put("enable", info.getEnable());
        json.put("schedule_id", schedule_id);
        OBJECT.getJSONArray("schedule").put(json);
        save();
        return true;
    }

    public void setSchedule(AlarmInfo info, int id) throws JSONException {
        JSONArray list = getSchedule();
        for (int i = 0; i < list.length(); i++) {
            JSONObject object = list.getJSONObject(i);
            if (object.getInt("schedule_id") != id) continue;
            JSONArray time = new JSONArray();
            time.put(info.getAlarmTime().getH());
            time.put(info.getAlarmTime().getM());
            time.put(info.getAlarmTime().getS());
            object.put("time", time);
            object.put("week", info.getWeek());
            object.put("enable", info.getEnable());
            object.put("schedule_id", id);
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
