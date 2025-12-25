package com.example.hourtracker.data;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.List;

public class WorkLogIO {

    private final WorkLogStore store;
    private final Gson gson = new Gson();

    public WorkLogIO(Context context) {
        store = new WorkLogStore(context);
    }

    // Export logs as JSONArray
    public JSONArray exportJson() {
        List<WorkLogStore.WorkEntry> logs = store.getAllLogs();
        try {
            return new JSONArray(gson.toJson(logs));
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    // Import logs replacing all data
    public void importJson(JSONArray array) {
        store.replaceAllLogs(array);
    }
}
