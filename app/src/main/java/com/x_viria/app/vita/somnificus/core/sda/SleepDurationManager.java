package com.x_viria.app.vita.somnificus.core.sda;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;

import com.x_viria.app.vita.somnificus.util.UUIDv7;
import com.x_viria.app.vita.somnificus.util.storage.Config;
import com.x_viria.app.vita.somnificus.util.storage.SPStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class SleepDurationManager {

    private final Context CONTEXT;
    private JSONObject DATA;

    public SleepDurationManager(Context context) throws JSONException {
        this.CONTEXT = context;
        String data = new SPStorage(CONTEXT).getString(Config.KEY__SLEEP_DURATION, "{'data': []}");
        this.DATA = new JSONObject(data);
        Log.d("SleepDurationManager", DATA.toString());
    }

    private JSONObject makeSDObject(SleepDurationInfo sdInfo) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", UUIDv7.randomUUID().toString());
        JSONObject time = new JSONObject();
        time.put("bed", sdInfo.getBedTime());
        time.put("wakeup", sdInfo.getWakeupTime());
        obj.put("time", time);
        JSONObject eval = new JSONObject();
        eval.put("g_or_b", sdInfo.EVAL__GOOD_OR_BAD);
        obj.put("eval", eval);
        return obj;
    }

    public void add(SleepDurationInfo sdInfo) throws JSONException {
        JSONObject obj = makeSDObject(sdInfo);
        DATA.getJSONArray("data").put(obj);
        save();
        sync();
    }

    public JSONArray add(JSONArray jsonArray, SleepDurationInfo sdInfo) throws JSONException {
        JSONObject obj = makeSDObject(sdInfo);
        jsonArray.put(obj);
        return jsonArray;
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
            sdinfo.ID = obj.getString("id");
            JSONObject eval = obj.getJSONObject("eval");
            sdinfo.EVAL__GOOD_OR_BAD = eval.getInt("g_or_b");
            list.add(sdinfo);
        }
        list.sort((o1, o2) -> {
            if (o1.getBedTime() < o2.getBedTime()) {
                return -1;
            } else if (o1.getBedTime() == o2.getBedTime()) {
                return Long.compare(o1.getWakeupTime(), o2.getWakeupTime());
            } else {
                return 1;
            }
        });
        Log.d("SleepDurationManager", String.format("Begin: %s (%d)", begin.getTime().toString(), begin.getTimeInMillis()));
        Log.d("SleepDurationManager", String.format("End: %s (%d)", end.getTime().toString(), end.getTimeInMillis()));
        List<SleepDurationInfo> target = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(list.get(i).getWakeupTime());
            if (begin.getTimeInMillis() <= c.getTimeInMillis() && c.getTimeInMillis() <= end.getTimeInMillis()) {
                Log.d("SleepDurationManager", String.format("Target: %s (%d)", c.getTime().toString(), c.getTimeInMillis()));
                target.add(list.get(i));
            }
        }
        return target;
    }

    public List<SleepDurationInfo> get(int period) throws JSONException {
        Calendar begin = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        switch (period) {
            case Period.ALL:
                begin.setTimeInMillis(0);
                break;
            case Period.LAST_7DAYS:
                begin.add(Calendar.DAY_OF_MONTH, -6);
                break;
            case Period.LAST_30DAYS:
                begin.add(Calendar.DAY_OF_MONTH, -29);
                break;
            default:
                throw new InvalidParameterException("");
        }
        return _get(begin, end);
    }

    public List<SleepDurationInfo> get(int period, int index) throws JSONException {
        Calendar begin = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        switch (period) {
            case Period.DAY:
                begin.add(Calendar.DAY_OF_MONTH, -1 * index);
                end.add(Calendar.DAY_OF_MONTH, -1 * index);
                break;
            case Period.WEEK:
                begin.add(Calendar.WEEK_OF_YEAR, -1 * index);
                begin.set(Calendar.DAY_OF_WEEK, 1);
                end.add(Calendar.WEEK_OF_YEAR, -1 * index);
                end.set(Calendar.DAY_OF_WEEK, 7);
                break;
            case Period.MONTH:
                begin.add(Calendar.MONTH, -1 * index);
                begin.set(Calendar.DAY_OF_MONTH, 1);
                end.add(Calendar.MONTH, -1 * index);
                end.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case Period.YEAR:
                begin.add(Calendar.YEAR, -1 * index);
                begin.set(Calendar.DAY_OF_YEAR, 1);
                end.add(Calendar.YEAR, -1 * index);
                end.set(Calendar.DAY_OF_YEAR, 365);
                break;
            default:
                throw new InvalidParameterException("");
        }
        return _get(begin, end);
    }

    public SleepDurationInfo get(String id) throws JSONException {
        List<SleepDurationInfo> sdlist = get(Period.ALL);
        for (SleepDurationInfo info : sdlist) {
            if (id.equals(info.ID)) return info;
        }
        return null;
    }

    public void remove(String id) throws JSONException {
        sync();
        List<SleepDurationInfo> sdlist = get(Period.ALL);
        List<SleepDurationInfo> copy = new ArrayList<>();
        for (int i = 0; i < sdlist.size(); i++) {
            if (sdlist.get(i).ID.equals(id)) continue;
            copy.add(sdlist.get(i));
        }
        JSONArray jsonArray = new JSONArray();
        for (SleepDurationInfo info : copy) {
            jsonArray = add(jsonArray, info);
        }
        DATA.put("data", jsonArray);
        save();
        sync();
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

        public static final int ALL = 0x001F;
        public static final int LAST_7DAYS = 0x0010;
        public static final int LAST_30DAYS = 0x0011;

    }

}
