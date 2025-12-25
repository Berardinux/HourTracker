package com.example.hourtracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkLogStore {

    private static final String PREF_NAME = "working_data";
    private static final String KEY_LOGS = "logs";

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    public WorkLogStore(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<WorkEntry> getAllLogs() {
        String json = prefs.getString(KEY_LOGS, "[]");

        Type listType = new TypeToken<ArrayList<WorkEntry>>(){}.getType();
        List<WorkEntry> logs = gson.fromJson(json, listType);

        if (logs == null) logs = new ArrayList<>(); // safety

        return logs;
    }

    public void saveLog(WorkEntry newEntry) {
        List<WorkEntry> logs = getAllLogs();

        // Check if there is a existing entry, and if there is write over top of the old entry.
        boolean found = false;
        for (int i = 0; i < logs.size(); i++) {
            WorkEntry existing = logs.get(i);
            if (existing.date != null && existing.date.equals(newEntry.date)) {
                logs.set(i, newEntry);
                found = true;
                break;
            }
        }

        // If there is not a existing entry add a new one.
        if (!found) {
            logs.add(newEntry);
        }

        // <Order the entry's>
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        logs.sort((a, b) -> {
            try {
                return sdf.parse(b.date).compareTo(sdf.parse(a.date));
            } catch (Exception e) {
                return 0;
            }
        });
        // </Order the entry's>

        prefs.edit()
                .putString(KEY_LOGS, gson.toJson(logs))
                .apply();
    }

    public static class WorkEntry {
        public String date;
        public float x1Hours;
        public float x1_5Hours;
        public float x2Hours;
        public float x2_5Hours;
        public int breakfast;
        public int lunch;
        public int dinner;
        public float baseRate;
        public float breakfastRate;
        public float lunchRate;
        public float dinnerRate;
        public float vacationHours;
        public float restHours;
    }

    // Replace all logs using imported json
    public void replaceAllLogs(JSONArray array) {
        prefs.edit().putString(KEY_LOGS, array.toString()).apply();
    }
}
