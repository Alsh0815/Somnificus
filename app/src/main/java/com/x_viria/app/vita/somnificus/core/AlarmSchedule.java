package com.x_viria.app.vita.somnificus.core;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class AlarmSchedule {

    private Context CONTEXT;
    private File FILE;
    private JSONObject OBJECT;

    public AlarmSchedule(Context context) throws JSONException, IOException {
        this.CONTEXT = context;

        FILE = new File(CONTEXT.getFilesDir(), "alarm_schedule.dat");
        if (!FILE.exists()) {
            FILE.createNewFile();
        }

        String data = "";
        String str;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8));
        while ((str = reader.readLine()) != null) {
            data = str + "\n";
        }
        reader.close();

        OBJECT = new JSONObject(data);
    }

    private void save() throws IOException {
        String json = OBJECT.toString();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8));
        writer.write(json, 0, json.length());
        writer.close();
    }

}
