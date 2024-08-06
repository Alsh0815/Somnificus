package com.x_viria.app.vita.somnificus.core.sda;

import android.content.Context;
import android.util.Log;

import com.x_viria.app.vita.somnificus.util.UUIDv7;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SleepDurationManager {

    private Context CONTEXT;
    private JSONObject DATA;

    public SleepDurationManager(Context context) throws JSONException {
        this.CONTEXT = context;
        String data = new SPStorage(CONTEXT).getString(Config.KEY__SLEEP_DURATION, "{'data': []}");
        this.DATA = new JSONObject(data);
        Log.d("SleepDurationManager", DATA.toString());
    }

    public boolean add(SleepDurationInfo sdInfo) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", UUIDv7.randomUUID().toString());
        JSONObject time = new JSONObject();
        time.put("bed", sdInfo.getBedTime());
        time.put("wakeup", sdInfo.getWakeupTime());
        obj.put("time", time);
        JSONObject eval = new JSONObject();
        eval.put("g_or_b", sdInfo.EVAL__GOOD_OR_BAD);
        obj.put("eval", eval);
        DATA.getJSONArray("data").put(obj);
        save();
        sync();
        return false;
    }

    private List<SleepDurationInfo> _get(Calendar begin, Calendar end) throws JSONException {
        List<SleepDurationInfo> list = new ArrayList<>();
        JSONArray objs = DATA.getJSONArray("data");
        for (int i = 0; i < objs.length(); i++) {
            JSONObject obj = objs.getJSONObject(i);
            JSONObject time = obj.getJSONObject("time");
            SleepDurationInfo sdinfo = new SleepDurationInfo(
                    time.getLong("bed"),
                    time.getLong("wakeup")
            );
            JSONObject eval = obj.getJSONObject("eval");
            sdinfo.EVAL__GOOD_OR_BAD = eval.getInt("g_or_b");
            list.add(sdinfo);
        }
        list.sort((o1, o2) -> {
            if (o1.getBedTime() < o2.getBedTime()) {
                return -1;
            } else if (o1.getBedTime() == o2.getBedTime()) {
                if (o1.getWakeupTime() < o2.getWakeupTime()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        });
        List<SleepDurationInfo> target = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Calendar cb = Calendar.getInstance();
            cb.setTimeInMillis(list.get(i).getBedTime());
            Calendar ce = Calendar.getInstance();
            ce.setTimeInMillis(list.get(i).getWakeupTime());
            if (0 <= cb.compareTo(begin) && ce.compareTo(end) <= 0) {
                target.add(list.get(i));
            }
        }
        return target;
    }

    public List<SleepDurationInfo> get(int period) throws JSONException {
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        switch (period) {
            case Period.LAST_7DAYS:
                begin.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case Period.LAST_30DAYS:
                begin.add(Calendar.DAY_OF_MONTH, -30);
                break;
            default:
                throw new InvalidParameterException("");
        }
        return _get(begin, end);
    }

    public List<SleepDurationInfo> get(int period, int index) throws JSONException {
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        switch (period) {
            case Period.DAY:
                begin.add(Calendar.DAY_OF_MONTH, -1 * index);
                end.add(Calendar.DAY_OF_MONTH, -1 * index);
                break;
            case Period.WEEK:
                begin.add(Calendar.WEEK_OF_YEAR, -1 * (index + 1));
                end.add(Calendar.WEEK_OF_YEAR, -1 * index);
                break;
            case Period.MONTH:
                begin.add(Calendar.MONTH, -1 * (index + 1));
                end.add(Calendar.MONTH, -1 * index);
                break;
            case Period.YEAR:
                begin.add(Calendar.YEAR, -1 * (index + 1));
                end.add(Calendar.YEAR, -1 * index);
                break;
            default:
                throw new InvalidParameterException("");
        }
        return _get(begin, end);
    }

    private void save() {
        new SPStorage(CONTEXT).setString(Config.KEY__SLEEP_DURATION, DATA.toString());
    }

    private void sync() throws JSONException {
        String data = new SPStorage(CONTEXT).getString(Config.KEY__SLEEP_DURATION, "{'data': []}");
        this.DATA = new JSONObject(data);
        Log.d("SleepDurationManager", DATA.toString());
    }

    public static class Period {

        public static final int DAY = 0x0000;
        public static final int WEEK = 0x0001;
        public static final int MONTH = 0x0002;
        public static final int YEAR = 0x0003;

        public static final int LAST_7DAYS = 0x0010;
        public static final int LAST_30DAYS = 0x0011;

    }

}
